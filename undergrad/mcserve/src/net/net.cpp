#include "mc/net/net.hpp"
#include "mc/net/fd.hpp"
#include "mc/async.hpp"

#include <cerrno>
#include <iostream>

#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#include <arpa/inet.h>
#include <string.h>
#include <fcntl.h>

#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/tcp.h>

#include <unistd.h>


struct resolve_hostname {
    addrinfo *res = nullptr;

    resolve_hostname(addrinfo *inp) : res(inp) {}

    resolve_hostname(const char *addr, const char *port) {
        addrinfo hints{};
        hints.ai_family = AF_UNSPEC;
        hints.ai_socktype = SOCK_STREAM;

        int gai_errno = getaddrinfo(addr, port, &hints, &res) != 0;
        if (gai_errno != 0) {
            std::cerr << "err on getaddrinfo: " << gai_strerror(gai_errno) << '\n';
            return;
        }
    }

    ~resolve_hostname() {
        if (res != nullptr)
            freeaddrinfo(res);
    }
};

void print_addrinfo(addrinfo *ainfo) {
    std::cout << " * ";
    char ipstr[INET6_ADDRSTRLEN] = {0};
    for (addrinfo *ptr = ainfo; ptr != nullptr; ptr = ptr->ai_next) {
        if (ptr->ai_family == AF_INET) {
            inet_ntop(ptr->ai_family,
                &(reinterpret_cast<sockaddr_in6 *>(ptr->ai_addr)->sin6_addr),
                ipstr, sizeof(ipstr));
            std::cout << "\tIPv4: " << ipstr << '\n';
        } else if (ptr->ai_family == AF_INET6) {
            inet_ntop(ptr->ai_family,
                &(reinterpret_cast<sockaddr_in *>(ptr->ai_addr)->sin_addr),
                ipstr, sizeof(ipstr));
            std::cout << "\tIPv6: " << ipstr << '\n';
        } else {
            std::cout << "\t????\n";
        }
    }
}

int mk_socket(addrinfo *serv) {
    int sock = socket(serv->ai_family, serv->ai_socktype, serv->ai_protocol);
    if (sock == -1) {
        std::cout << "err on socket: " << strerror(errno) << '\n';
        return -1;
    }

    if (fcntl(sock, F_SETFL, O_NONBLOCK) == -1) {
        std::cout << "err on fcntl: " << strerror(errno) << '\n';
        close(sock);
        return -1;
    }

    // disable nagle's algorithm as per
    // https://minecraft.wiki/w/Java_Edition_protocol/FAQ#...some_of_the_packets_I_expect_to_receive_seem_to_be_missing_or_too_short
    int yes = 1;
    if (setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, &yes, sizeof(yes)) == -1)
        std::cout << "cannot disable nagle: " << strerror(errno) << '\n';
    return sock;
}

namespace mc {
    tcp_server::tcp_server(const char *addr, const char *port, thread_pool *pool) : pool(pool) {
        std::cout << "starting server on " << addr << ":" << port << '\n';
        auto host = resolve_hostname(addr, port);
        addrinfo *serv = host.res;
        if (serv == nullptr) return;
        print_addrinfo(serv);
        sock = mk_socket(serv);

        if (bind(sock, serv->ai_addr, serv->ai_addrlen) == -1) {
            std::cout << "err on bind: " << strerror(errno) << '\n';
            close(sock);
            sock = -1;
            return;
        }

        if (listen(sock, 5) == -1) {
            std::cout << "err on listen: " << strerror(errno) << '\n';
            close(sock);
            sock = -1;
        }
    }

    tcp_server::~tcp_server() {
        if (sock != -1)
            close(sock);
    }

    static pool_future handle_connection(int client, sockaddr *addr, socklen_t addrlen, tcp_server *serv);

    pool_future tcp_server::accept_loop(tcp_server *serv) {
        while (true) {
            co_await io_awaiter{serv->sock, POLLIN};

            sockaddr_storage addr_storage{};
            socklen_t addrlen = sizeof(addr_storage);
            sockaddr *addr = reinterpret_cast<sockaddr *>(&addr_storage);
            int client = accept(serv->sock, addr, &addrlen);
            if (client == -1) {
                if (errno != EAGAIN && errno != EWOULDBLOCK)
                    std::cout << "err on accept: " << strerror(errno) << '\n';
                continue;
            }

            std::cout << "connection ";
            char ipstr[INET6_ADDRSTRLEN];
            if (addr->sa_family == AF_INET) {
                std::cout << "IPv4 ";
                inet_ntop(addr->sa_family,
                    &(reinterpret_cast<sockaddr_in *>(addr)->sin_addr), ipstr, sizeof(ipstr));
            } else if (addr->sa_family == AF_INET6) {
                std::cout << "IPv6 ";
                inet_ntop(addr->sa_family,
                    &(reinterpret_cast<sockaddr_in *>(addr)->sin_addr), ipstr, sizeof(ipstr));
            }
            std::cout << ipstr << '\n';

            serv->pool->queue(handle_connection(client, addr, addrlen, serv));
        }
    }
}

using namespace mc;


pool_future mc::handle_connection(int client, sockaddr *addr, socklen_t addrlen,
                                  tcp_server *serv) {
    // copy it into our frame cause the object is about to be destroyed
    // on our parents frame. then we do our initial suspend.
    sockaddr_storage addr_store;
    memcpy(&addr_store, addr, addrlen);
    addr = reinterpret_cast<sockaddr *>(&addr_store);
    co_await std::suspend_always{};

    fd_reader rbuf{client};

    ssize_t bytes = -1;
    while (bytes != 0) {
        do {
            bytes = co_await rbuf.recv();
        } while (bytes == -1);

        *rbuf.end = '\0';
        std::cout << "recv " << bytes << ": " << rbuf.head << '\n';
    }
    std::cout << "disconnect\n";
}
