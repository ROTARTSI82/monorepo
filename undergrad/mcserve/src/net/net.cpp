#include "mc/net/net.hpp"

#include <iostream>

#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#include <arpa/inet.h>
#include <string.h>

#include <sys/socket.h>
#include <netinet/tcp.h>

#include <unistd.h>

#include "mc/async.hpp"

struct resolve_hostname {
    addrinfo *res = nullptr;

    resolve_hostname(addrinfo *inp) : res(inp) {}

    resolve_hostname(const char *addr, const char *port) {
        addrinfo hints = {0};
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

namespace mc {
    future<int, noalloc_promise<int>> test() {
        co_await std::suspend_always{};
        co_return 5;
    };

    tcp_server::tcp_server(const char *addr, const char *port) {
        std::cout << "starting server on " << addr << ":" << port << '\n';
        auto host = resolve_hostname(addr, port);
        addrinfo *serv = host.res;
        if (serv == nullptr) return;
        print_addrinfo(serv);

        // just use the first entry lol
        sock = socket(serv->ai_family, serv->ai_socktype, serv->ai_protocol);
        if (sock == -1) {
            std::cout << "err on socket: " << strerror(errno) << '\n';
            return;
        }

        // disable nagle's algorithm as per
        // https://minecraft.wiki/w/Java_Edition_protocol/FAQ#...some_of_the_packets_I_expect_to_receive_seem_to_be_missing_or_too_short
        int yes = 1;
        if (setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, &yes, sizeof(yes)) == -1)
            std::cout << "cannot disable nagle: " << strerror(errno) << '\n';

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
}
