#include "csc/TcpServer.hpp"

#include <cerrno>
#include <cstring>

#include <unistd.h>
#include <fcntl.h>

#include <stdexcept>
#include <fstream>

#include <zlib.h>

#include "csc/varint.hpp"
#include "csc/misc_defs.hpp"
#include "csc/log.hpp"

#define TRY_NEXT it = it->ai_next; continue;
#define TRY_SYSCALL(expr, onfail) if (expr == -1) {CEL_ERROR(#expr " failed: {}", strerror(errno)); onfail}

#define SYSCALL_SEND dbg_send


static ssize_t dbg_send(int sock, const void *buf, size_t len, int flags) {
    CEL_DEBUG("New outbound packet");
    for (int i = 0; i < len; i++)
        CEL_DEBUG("\t{:#0x}", reinterpret_cast<const uint8_t *>(buf)[i]);

    return send(sock, buf, len, flags);
};

namespace csc {

    TcpServer::TcpServer(const char *ip, const char *port) {
        int do_reuseaddr = true;
        constexpr int listen_backlog = 16;

        addrinfo hints{};
        hints.ai_family = AF_UNSPEC;     // don't care IPv4 or IPv6
        hints.ai_socktype = SOCK_STREAM; // TCP stream sockets
        hints.ai_flags = AI_PASSIVE;     // fill in my IP for me

        addrinfo *servinfo, *it;  // will point to the results
        int status = getaddrinfo(ip, port, &hints, &servinfo);
        if (status != 0) {
            CEL_ERROR("getaddrinfo() failed: {}", gai_strerror(status));
            throw std::runtime_error{"getaddrinfo() failed"};
        }

        it = servinfo;
        while (true) {
            if (it == nullptr) { // we reached the end of the linked list. nothing worked.
                freeaddrinfo(servinfo); 
                throw std::runtime_error{"Exhausted all options returned by getaddrinfo()"};
            }

            sock = socket(it->ai_family, it->ai_socktype, it->ai_protocol);
            TRY_SYSCALL(sock, TRY_NEXT)
            TRY_SYSCALL(fcntl(sock, F_SETFL, O_NONBLOCK), TRY_NEXT)
            TRY_SYSCALL(do_reuseaddr && setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &do_reuseaddr, sizeof(do_reuseaddr)), {})
            TRY_SYSCALL(bind(sock, it->ai_addr, it->ai_addrlen), TRY_NEXT)

            break; // success! it points to the addrinfo we selected (that works)
        }

        TRY_SYSCALL(listen(sock, listen_backlog), {
            freeaddrinfo(servinfo);
            throw std::runtime_error{"listen() failed"};
        })

        freeaddrinfo(servinfo);

        CEL_INFO("Server up");
    }

    TcpServer::~TcpServer() {
        CEL_INFO("~TcpServer");
        if (close(sock) == -1) {
            fprintf(stderr, "close() failed: %s\n", strerror(errno));
        }
    }

    void TcpServer::tickAccept() {
        sockaddr_storage accept_sock;
        socklen_t sock_size = sizeof(accept_sock);

        ClientData dat;

        dat.sock = accept(sock, (sockaddr *) &accept_sock, &sock_size);

        if (dat.sock == -1) {
            if (errno == EAGAIN || errno == EWOULDBLOCK)
                return; // no connections to accept

            CEL_ERROR("accept() failed: {}", strerror(errno));
            return; // throw std::runtime_error{"accept() failed"};
        }

        // handle client connect
        CEL_INFO("New client connected");

        clients.emplace_back(dat);
    }

    void TcpServer::dropConnection(int cliRef) {
        CEL_INFO("Close connection");
        TRY_SYSCALL(close(clients[cliRef].sock), {});
        clients.erase(clients.begin() + cliRef);
    }

    void TcpServer::tickClient(int cliRef) {
        constexpr int recv_buf_size = 2097151;
        int8_t buf[recv_buf_size];

        HandlingInfo hand{};

        int rsize = recv(clients[cliRef].sock, buf, recv_buf_size, 0);
        if (rsize == -1) {
            if (errno != EAGAIN && errno != EWOULDBLOCK) {
                CEL_ERROR("recv() failed: {}", strerror(errno));
                dropConnection(cliRef);
            }
            return; // no data to handle
        } else if (rsize == 0) {
            // handle shutdown!
            CEL_ERROR("Empty packet");
            dropConnection(cliRef);
            return;
        }

        // handle packet recieve
        CEL_INFO("New Packet");
        for (int i = 0; i < rsize; i++) {
            CEL_INFO("\t{:#0x}", reinterpret_cast<unsigned char *>(buf)[i]);
        }

        int8_t *toDel = nullptr; // deleting nullptr has no effect

        hand.cliRef = cliRef;
        hand.dat = buf;
        hand.len = readVarInt(&hand.dat); // Total length of packet, excluding this field
        if ((buf + rsize) - hand.dat != hand.len) { 
            CEL_ERROR("Incomplete packet recieved! Expected {} bytes but got {}", hand.len, (buf + rsize) - hand.dat);
            // dropConnection(cliRef);
        } else {
            CEL_DEBUG("got {} of {} bytes", hand.len, (buf + rsize) - hand.dat);
        }

        if (clients[cliRef].compressed) {
            CEL_ERROR("Cannot handle compressed packets yet");
            int dataLength = readVarInt(&hand.dat);
            if (dataLength > 0) {
                toDel = new int8_t[dataLength];

                // TODO zlib decompress from bIt into ret here
                uLong actualLen = dataLength;
                if (uncompress((Bytef *) toDel, &actualLen, (Bytef *) hand.dat, dataLength) != Z_OK) {
                    CEL_ERROR("Deflate failed! Dropping packet");
                    delete[] toDel;
                    return;
                }

                if (actualLen != dataLength) CEL_ERROR("Deflate error: Expected {} but got {} bytes", dataLength, actualLen);

                hand.dat = toDel;
                hand.len = dataLength; // this is different. :/ sure to cause a bug later
            }
        }

        hand.id = readVarInt(&hand.dat);
        CEL_INFO("Packet id = {:#0x}", hand.id);
        // bIt now points to the raw data 

        switch (clients[cliRef].state) {
        case ClientState::handshake:
            handleHandshake(hand);
            break;
        case ClientState::status: {
            if (hand.id == (int) ServBoundStatusPid::ping) {
                CEL_WARN("PING");
                TRY_SYSCALL(SYSCALL_SEND(clients[cliRef].sock, buf, rsize, 0), dropConnection(cliRef);); // resend the same thing back lol
                break;
            }
            handleStatus(hand);
            break;
        }
        case ClientState::login:
            handleLogin(hand);
            break;

        case ClientState::play:
            // playHandler.handle(bIt);
            // break;

        default:
            CEL_ERROR("Invalid client state!");
            dropConnection(cliRef);
        }

        delete[] toDel;
    }


    inline void TcpServer::handleHandshake(HandlingInfo &info) {
        switch (static_cast<HandshakePid>(info.id)) {
        case HandshakePid::handshaking: {
            int protocolVersion = readVarInt(&info.dat);
            std::string hostname = readString(&info.dat);
            uint16_t port = ntohs(*reinterpret_cast<uint16_t *>(info.dat)); info.dat += 2;
            int nextState = readVarInt(&info.dat);
            CEL_INFO("HANDSHAKING: Protocol {} onto {}:{}  nextstate = {}", protocolVersion, hostname, port, nextState);
            if (protocolVersion != 756) dropConnection(info.cliRef);
            clients[info.cliRef].state = static_cast<ClientState>(nextState);
            break;
        }
        case HandshakePid::legacyServerPing:
            CEL_WARN("No support for legacy server pings yet");
            break;

        default:
            CEL_ERROR("Invalid handshake packet id");
            dropConnection(info.cliRef);
        }
    }

    inline void TcpServer::handleStatus(HandlingInfo &info) {
        switch (static_cast<ServBoundStatusPid>(info.id)) {
        case ServBoundStatusPid::request: {
            std::string response = "{"
    "\"version\": {"
    "    \"name\": \"1.17.1\","
    "    \"protocol\": 756"
    "},"
    "\"players\": {"
    "    \"max\": 100,"
    "    \"online\": 5,"
    "    \"sample\": ["
    "        {"
    "            \"name\": \"thinkofdeath\","
    "            \"id\": \"4566e69f-c907-48ee-8d71-d7ba5aa00d20\""
    "        }"
    "    ]"
    "},"
    "\"description\": {"
    "    \"text\": \"Hello world\""
    "},"
    "\"favicon\": \"data:image/png;base64,<data>\" } ";

            auto fac = HIC.newPacketFactory((int) CliBoundStatusPid::response, static_cast<unsigned>(5 + response.size()));
            int8_t *it = fac.get();
            it = writeVarInt(response.size(), it);
            memcpy(it, response.data(), response.size());
            auto [ptr, size] = fac.construct(it - fac.get() + response.size());
            TRY_SYSCALL(SYSCALL_SEND(clients[info.cliRef].sock, ptr, size, 0), 
                        dropConnection(info.cliRef););

            break;
        }

        default:
            CEL_ERROR("Invalid status packet");
            dropConnection(info.cliRef);
        }
    }

    inline void TcpServer::handleLogin(HandlingInfo &info) {
        switch (static_cast<ServBoundLoginPid>(info.id)) {
        case (ServBoundLoginPid::loginStart): {
            std::string playerName = readString(&info.dat);
            CEL_WARN("{} log in", playerName);

            // normally, you'd go through some encryption stuff, but that's bullshit.
            // just using offline mode rn

            // auto ecFac = HIC.newPacketFactory((int) CliBoundLoginPid::setCompression, 5);
            // auto it = writeVarInt(COMPRESSION_THRESHOLD, ecFac.get());
            // auto [ ecPtr, ecSize ] = ecFac.construct(it - ecFac.get());
            // TRY_SYSCALL(SYSCALL_SEND(clients[info.cliRef].sock, ecPtr, ecSize, 0), 
            //             dropConnection(info.cliRef););

            // HIC.compressed = true;

            auto fac = HIC.newPacketFactory((int) CliBoundLoginPid::loginSuccess, playerName.size() + 21);
            reinterpret_cast<size_t *>(fac.get())[0] = std::hash<std::string>()(playerName);
            reinterpret_cast<size_t *>(fac.get())[1] = std::hash<std::string>()("OfflinePlayer:" + playerName); // too lazy to implement proper uuidv3
            auto it = fac.get() + 16;
            it = writeVarInt(playerName.size(), it);
            memcpy(it, playerName.data(), playerName.size());

            auto [ ptr, sendSize ] = fac.construct(it - fac.get() + playerName.size());
            TRY_SYSCALL(SYSCALL_SEND(clients[info.cliRef].sock, ptr, sendSize, 0), 
                        dropConnection(info.cliRef););
        
            clients[info.cliRef].state = ClientState::play;
            
            CEL_INFO("Login success sent");

            // next, send "Join Game"
            // *plugin stuff*
            // "Server Difficulty"
            // "Player Abilities"
            // "Held Item Change"
            // "Entity Properties"
            // *bugged packet?*
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Entity Head Look"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Entity Head Look"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data" No. 139

            // Cli -> Serv: "Client Settings"
            // *cli -> serv paper plugin stuff*

            // "Update Light" No. 145
            // "Chunk Data"

            // "Entity Metadata" No. 149

            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"

            // "Entity Head Look" No. 170

            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"
            // "Update Light"
            // "Chunk Data"

            // "Time Update" No. 188

            // "Update Light" & "Chunk Data" x 10

            // "Block Change" No. 230
            // some more light & chunk updates
            // "Block Change" No. 252
            // ...
            // "Block Change" No. 274
            // ...
            // "Entity Head Look" No. 294
            // "Entity Head Look" No. 318
            // "Entity Head Look" No. 360, No. 382 
            // "Block Change" No. 404
            // "Entity Head Look" No. 426 No. 448

            // Cli -> Serv: "Teleport Confirm" No. 454? But "Player Position And Look" was not sent?
            // Cli -> Serv: "Player Position and Rotation" No. 456

            // no 537: Block Change
            // no 558 & 560: Block Change, no. 562: "Entity Position"

            // Cli -> Serv: Player Position and Rotation No. 614

            break;
        }
        default:
            CEL_ERROR("Invalid login packet");
            dropConnection(info.cliRef);
        }
    }
}