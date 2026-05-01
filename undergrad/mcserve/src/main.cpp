#include "mc/net/net.hpp"
#include "mc/async.hpp"

#include <coroutine>
#include <thread>
#include <unistd.h>
#include <limits.h>

#include <iostream>

using namespace std::literals;


int main() {
    int nothreads = std::thread::hardware_concurrency();
    mc::thread_pool pool(nothreads);
    std::mutex mtx;
    auto task = [&](int t) -> mc::pool_future {
        for (int i = 0; i < 10; i++) {
            {
                std::unique_lock<std::mutex> lg(mtx);
                std::cout << "task " << t << " on " << std::this_thread::get_id() << "\n";
            }
            std::this_thread::sleep_for(50ms);
            co_await std::suspend_always{};
        }
    };

    char hostname[HOST_NAME_MAX];
    gethostname(hostname, sizeof(hostname));
    mc::tcp_server serv{hostname, "8000", &pool};
    std::cout << "fun!\n";

    // for (int i = 0; i < nothreads; i++) {
    //     pool.queue(task(i));
    // }
    pool.queue(mc::tcp_server::accept_loop(&serv));

    int x = -1;
    while (x != 0) {
        std::cin >> x;
        std::cout << x;
    }
}
