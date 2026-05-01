#include "mc/async.hpp"
#include <iostream>
#include <string.h>

namespace mc {
    void thread_pool::worker_thread_fun(thread_pool *pool, std::stop_token stop) {
        while (!stop.stop_requested()) {
            pool_task job;
            {
                std::unique_lock<std::mutex> lg(pool->task_mtx);
                std::cout << "tasks: " << pool->tasks.size() << '\n';
                if (pool->tasks.empty()) {
                    pool->ready.wait(lg, [&]() {
                        return stop.stop_requested() || !pool->tasks.empty();
                    });
                }
                if (pool->tasks.empty())
                    continue;
                job = pool->tasks.front();
                pool->tasks.pop();
            }
            if (!job.done())
                job.resume();

            if (job.promise().requeue) {
                if (!job.done())
                    pool->queue(job);
                else
                    job.destroy();
            }
        }
    }

    void thread_pool::io_thread_fun(thread_pool *pool, std::stop_token stop) {
        while (!stop.stop_requested()) {
            std::cout << "io thread heartbeat polling " << pool->events.size() << '\n';
            if (poll(pool->events.data(), pool->events.size(), 1000) == -1)
                std::cout << "err on poll: " << strerror(errno) << '\n';

            std::unique_lock<std::mutex> lg(pool->event_mtx);
            for (size_t i = 0; i < pool->events.size(); i++) {
                if (pool->events[i].revents
                        && i < pool->event_listeners.size()
                        && pool->event_listeners[i]) {
                    pool_task to_resume = pool->event_listeners[i];

                    // O(n^2), can probably be made more efficient.
                    pool->event_listeners.erase(pool->event_listeners.begin() + i);
                    pool->events.erase(pool->events.begin() + i);
                    i--;

                    lg.unlock();
                    pool->queue(to_resume);
                    lg.lock();
                    std::cout << "io resumed " << i << '\n';
                }
            }
        }
    }

    thread_pool::thread_pool(int num) {
        threads.reserve(num);
        threads.emplace_back(io_thread_fun, this, stop.get_token());
        for (int i = 1; i < num; i++)
            threads.emplace_back(worker_thread_fun, this, stop.get_token());
    }

    thread_pool::~thread_pool() {
        stop.request_stop();
        ready.notify_all();
        for (auto &thread : threads)
            thread.join();
        while (!tasks.empty()) {
            tasks.front().destroy();
            tasks.pop();
        }

        for (const auto h : event_listeners)
            h.destroy();
    }
}
