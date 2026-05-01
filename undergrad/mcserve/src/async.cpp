#include "mc/async.hpp"

namespace mc {
    void thread_pool::worker_thread_fun(thread_pool *pool, std::stop_token stop) {
        while (!stop.stop_requested()) {
            pool_handle job;
            {
                std::unique_lock<std::mutex> lg(pool->mtx);
                if (pool->tasks.empty()) {
                    pool->ready.wait(lg, [&]() {
                        return stop.stop_requested() || !pool->tasks.empty();
                    });
                }
                if (pool->tasks.empty())
                    continue;
                job = pool->tasks.top();
                pool->tasks.pop();
            }
            if (!job.done()) {
                job.resume();
                if (!job.done())
                    pool->raw_queue(job);
                else if (job.promise().destroy)
                    job.destroy();
            } else if (job.promise().destroy) {
                job.destroy();
            }
        }
    }

    thread_pool::thread_pool(int num) {
        threads.reserve(num);
        for (int i = 0; i < num; i++)
            threads.emplace_back(worker_thread_fun, this, stop.get_token());
    }

    thread_pool::~thread_pool() {
        stop.request_stop();
        ready.notify_all();
        for (auto &thread : threads)
            thread.join();
        while (!tasks.empty()) {
            tasks.top().destroy();
            tasks.pop();
        }
    }
}
