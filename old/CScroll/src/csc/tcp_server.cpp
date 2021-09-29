#include "csc/tcp_server.hpp"
#include "csc/log.hpp"

#include <cerrno>
#include <cstring>

#include <stdexcept>
#include <unistd.h>
#include <fcntl.h>

#include <fstream>

#include <zlib.h>
#include "csc/varint.hpp"

#define TRY_NEXT it = it->ai_next; continue;
#define TRY_SYSCALL(expr, onfail) if (expr == -1) {CEL_ERROR(#expr " failed: {}", strerror(errno)); onfail}


#define COMPRESSION_THRESHOLD 512

namespace csc {

    TcpServer::TcpServer(const char *ip, const char *port) {
        bool do_reuseaddr = true;
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
                TRY_SYSCALL(send(clients[cliRef].sock, buf, rsize, 0), dropConnection(cliRef);); // resend the same thing back lol
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

            PacketFactory fac{(int) CliBoundStatusPid::response, static_cast<unsigned>(5 + response.size())};
            int8_t *it = fac.get();
            it = writeVarInt(response.size(), it);
            memcpy(it, response.data(), response.size());
            auto [ptr, size] = fac.construct(it - fac.get() + response.size());
            TRY_SYSCALL(send(clients[info.cliRef].sock, ptr, size, 0), 
                        dropConnection(info.cliRef););

            CEL_INFO("Packet len = {}", readVarInt(&ptr));
            CEL_INFO("Packet id = {:#0x}", readVarInt(&ptr));
            CEL_INFO("String len = {} (real={})", readVarInt(&ptr), response.size());

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
            CEL_DEBUG("{} log in", playerName);
            // normally, you'd go through some encryption stuff, but that's bullshit.
            // just using offline mode rn

            PacketFactory ecFac((int) CliBoundLoginPid::setCompression, 5);
            auto it = writeVarInt(COMPRESSION_THRESHOLD, ecFac.get());
            auto [ ecPtr, ecSize ] = ecFac.construct(it - ecFac.get());
            TRY_SYSCALL(send(clients[info.cliRef].sock, ecPtr, ecSize, 0), 
                        dropConnection(info.cliRef););

            CompressedPacketFactory fac((int) CliBoundLoginPid::loginSuccess, playerName.size() + 21);
            reinterpret_cast<size_t *>(fac.get())[0] = std::hash<std::string>()(playerName);
            reinterpret_cast<size_t *>(fac.get())[1] = std::hash<std::string>()(playerName); // too lazy to implement proper uuidv3
            it = fac.get() + 16;
            it = writeVarInt(playerName.size(), it);
            memcpy(it, playerName.data(), playerName.size());

            auto [ ptr, sendSize ] = fac.construct(it - fac.get() + playerName.size());
            TRY_SYSCALL(send(clients[info.cliRef].sock, ptr, sendSize, 0), 
                        dropConnection(info.cliRef););

            CEL_INFO("Packet Len = {} (ss={}), Data Len = {}, pack id = {}", readVarInt(&ptr), sendSize, readVarInt(&ptr), readVarInt(&ptr));
        
            clients[info.cliRef].state = ClientState::play;
            
            CEL_INFO("Login success sent");
            break;
        }
        default:
            CEL_ERROR("Invalid login packet");
            dropConnection(info.cliRef);
        }
    }

    PacketFactory::PacketFactory(int id, unsigned size) {
        store = new int8_t[size + 10]; // + 10 for 2 var ints: packet id and packet size
        auto end = writeVarInt(id, store);
        auto idSize = end - store;

        headroom = 10 - idSize;

        // copy it over
        for (int i = 1; i <= idSize; i++)
            store[10 - i] = store[idSize - i];
    }
    PacketFactory::~PacketFactory() {
        delete[] store;
    }

    inline int8_t *PacketFactory::get() {
        return store + 10;
    }

    std::pair<int8_t *, unsigned> PacketFactory::construct(int newsize) {
        newsize += 10 - headroom; // account for packet id
        auto end = writeVarInt(newsize, store);
        auto size = end - store;

        for (int i = 1; i <= size; i++)
            store[headroom - i] = store[size - i];

        CEL_INFO("construct() = {}, {}", headroom - size, size + newsize);
        return std::make_pair<int8_t *, unsigned>(store + headroom - size, size + newsize);
    }



    CompressedPacketFactory::CompressedPacketFactory(int id, unsigned size) {
        store = new int8_t[size + 15]; // + 5 for 1 var int: packet id
        auto end = writeVarInt(id, store);
        auto idSize = end - store;

        headroom = 15 - idSize;

        // copy it over
        for (int i = 1; i <= idSize; i++)
            store[15 - i] = store[idSize - i];
    }
    CompressedPacketFactory::~CompressedPacketFactory() {
        delete[] store;
    }

    inline int8_t *CompressedPacketFactory::get() {
        return store + 15;
    }

    std::pair<int8_t *, unsigned> CompressedPacketFactory::construct(int newsize) {
        if (newsize > COMPRESSION_THRESHOLD || true) {
            CEL_INFO("Packet is being compressed");
            int8_t *compressed = new int8_t[newsize + 5];
            uLong cmpSize = newsize + 5;
            auto dataLen = newsize + (15 - headroom);
            if (compress2((Bytef *) compressed, &cmpSize, (Bytef *) store + headroom, dataLen, Z_DEFAULT_COMPRESSION) != Z_OK) {
                CEL_ERROR("zlib compression fail!");
                delete[] compressed;
                return std::make_pair(store, 0U); // dummy payload
            }

            memcpy(store + 10, compressed, cmpSize);
            delete[] compressed;

            // write the Data Length and copy it up to the 10th byte
            auto end = writeVarInt(dataLen, store);
            auto size = end - store;
            for (int i = 1; i <= size; i++)
                store[10 - i] = store[size - i];

            headroom = 10 - size;
            end = writeVarInt(size + dataLen, store); // total packet length
            auto plenSize = end - store;
            for (int i = 1; i <= plenSize; i++)
                store[headroom - i] = store[plenSize - i];

            return std::make_pair<int8_t *, unsigned>(store + headroom - size, size + plenSize + cmpSize);
        }

        CEL_INFO("Packet is NOT compressed");

        // here, headroom is the index of the begin
        CEL_INFO("packet id at {}", headroom);
        store[headroom - 1] = 0; // data length is 0
        headroom--;

        // write packet size and shift it over
        // (15 - headroom) is the size of the packet id
        auto end = writeVarInt(newsize + (15 - headroom), store);
        auto size = end - store;

        // for (int i = 0; i < size + 1; i++)
        //     CEL_INFO("WrittenVarInt[{}] = {:#0x}", i, store[i]);

        for (int i = 1; i <= size; i++)
            store[headroom - i] = store[size - i];

        // for (int i = 0; i < newsize; i++)
        //     CEL_INFO("FinalPacket[{}] = {:#0x}", i, store[i]);

        // CEL_INFO("hr - size = {}", headroom - size);

        return std::make_pair<int8_t *, unsigned>(store + headroom - size, size + newsize + (15 - headroom));
    }
}