#include "mc/async.hpp"
#include <cstdint>

namespace mc {
    void thread_worker(thread_pool &pool, std::stop_token stop) {
        while (!stop.stop_requested()) {
            std::coroutine_handle<> run;
            {
                std::unique_lock<std::mutex> lg(pool.mtx);
                if (pool.tasks.empty()) {
                    pool.ready.wait(lg, [&]() {
                        return stop.stop_requested() || !pool.tasks.empty();
                    });
                }
                run = pool.tasks.top().coro;
                pool.tasks.pop();
            }
            if (!run.done())
                run.resume();

        }
    }

    thread_pool::thread_pool(int num) {
        threads.reserve(num);
        for (int i = 0; i < num; i++)
            threads.emplace_back(thread_worker, *this, stop.get_token());
    }

    thread_pool::~thread_pool() {}
}
