#include "mc/net/net.hpp"
#include "mc/async.hpp"

#include <thread>
#include <unistd.h>
#include <limits.h>

#include <iostream>

using namespace std::literals;
using namespace mc;

pool_future<int> fib(int test) {
    if (test <= 1) co_return 1;
    int ret = (co_await fib(test - 1)) + (co_await fib(test - 2));
    std::cout << "fib " << test << " = " << ret << '\n';
    co_return ret;
}


int main() {
    int nothreads = static_cast<int>(std::thread::hardware_concurrency());
    mc::thread_pool pool(nothreads);

    char hostname[HOST_NAME_MAX];
    gethostname(hostname, sizeof(hostname));
    mc::tcp_server serv{hostname, "8000", &pool};
    if (serv.sock == -1) {
        std::cout << "server did not start\n";
        return 1;
    }

    pool.queue(fib(64));

    pool.queue(mc::tcp_server::accept_loop(&serv));

    int x = -1;
    while (x != 0) {
        std::cin >> x;
        std::cout << x;
    }
}
