#include "mc/net/net.hpp"

#include <unistd.h>
#include <bits/local_lim.h>

#include <iostream>

int main() {
    char hostname[HOST_NAME_MAX];
    gethostname(hostname, sizeof(hostname));
    mc::tcp_server serv{hostname, "8000"};
    std::cout << "fun!\n";
}
