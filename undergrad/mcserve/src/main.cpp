#include "mc/net/net.hpp"
#include "mc/async.hpp"

#include <thread>
#include <unistd.h>
#include <limits.h>

#include <iostream>

using namespace std::literals;
using namespace mc;

pool_future<int> fib(int test, timer *time) {
    if (test <= 1) co_return 1;
    int ret = (co_await fib(test - 1, time)) + (co_await fib(test - 2, time));
    std::cout << "fib " << test << " = " << ret << '\n';
    co_await time->sleep(0, 100 *1000000);
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

    timer time{};
    pool.queue(fib(8, &time));

    pool.queue(mc::tcp_server::accept_loop(&serv));

    timer interval{};
    pool.queue(interval.set_interval(2, 0,
                []() { std::cout << "lmfao\n"; }));

    int x = -1;
    while (x != 0) {
        std::cin >> x;
        std::cout << x;
    }
}
