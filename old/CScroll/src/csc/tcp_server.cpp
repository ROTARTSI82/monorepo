#include "csc/tcp_server.hpp"
#include "csc/log.hpp"

#include <cerrno>
#include <cstring>

#include <stdexcept>
#include <unistd.h>
#include <fcntl.h>

#define TRY_NEXT it = it->ai_next; continue;
#define TRY_SYSCALL(expr, onfail) if (expr == -1) {CEL_ERROR(#expr " failed: {}", strerror(errno)); onfail}

namespace csc {

    TcpServer::TcpServer(const char *ip, const char *port) {
        bool do_reuseaddr = false;
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

    void TcpServer::tickClients(int num) {
        if (clients.empty()) return; // remove this check?
        constexpr int recv_buf_size = 2097151;
        char buf[recv_buf_size];

        auto ncli = clients.size();
        for (int i = 0; i < num; i++) {
            int rsize = recv(clients[update_it].sock, buf, recv_buf_size, 0);
            if (rsize == -1) {
                if (errno != EAGAIN && errno != EWOULDBLOCK) {
                    CEL_ERROR("recv() failed: {}", strerror(errno));
                }
                update_it = (update_it + 1) % ncli;
                continue;
            } else if (rsize == 0) {
                // handle shutdown!

                CEL_INFO("Close connection");

                TRY_SYSCALL(close(clients[update_it].sock), {});

                clients.erase(clients.begin() + update_it);
                if ((ncli = clients.size()) == 0) { update_it = 0; return; }
                update_it %= ncli;
                continue;
            }

            // handle packet recieve
            CEL_INFO("New Packet");
            for (int i = 0; i < rsize; i++) {
                CEL_INFO("\t{:#0x}", reinterpret_cast<unsigned char *>(buf)[i]);
            }


            update_it = (update_it + 1) % ncli;
        }
    }
}