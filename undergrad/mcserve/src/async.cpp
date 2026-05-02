#include "mc/async.hpp"
#include <atomic>
#include <cassert>
#include <fcntl.h>
#include <iostream>
#include <stop_token>
#include <string.h>
#include <unistd.h>

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
            job.promise().requeue = requeue_mode::READY;
            if (!job.done())
                job.resume();

            requeue_mode mode = requeue_mode::IO_BLOCKED;
            if (job.promise().requeue.compare_exchange_strong(
                    mode, requeue_mode::READY, std::memory_order_relaxed))
                // an io_worker_thread will now reschedule this task,
                // now that we have marked it as ready
                continue;

            assert(mode == requeue_mode::READY);
            if (!job.done())
                pool->queue(job);
            else
                job.destroy();
        }
    }

    void thread_pool::io_thread_fun(thread_pool *pool, std::stop_token stop) {
        std::vector<pollfd> events{};
        std::vector<pool_task> listeners{};

        while (!stop.stop_requested()) {
            {
                std::unique_lock<std::mutex> lg(pool->event_mtx);
                events.reserve(events.size() + pool->events.size());
                events.insert(events.end(), pool->events.begin(), pool->events.end());
                listeners.reserve(listeners.size() + pool->event_listeners.size());
                listeners.insert(listeners.end(),
                    pool->event_listeners.begin(), pool->event_listeners.end());
                pool->event_listeners.clear();
                pool->events.clear();
                std::cout << "io thread heartbeat polling "
                    << events.size() << " : "
                    << listeners.size() << '\n';
            }
            if (poll(events.data(), events.size(), 1000) == -1)
                std::cout << "err on poll: " << strerror(errno) << '\n';

            for (size_t i = 0; i < events.size(); i++) {
                if (events[i].revents
                        && i < listeners.size()
                        && listeners[i]
                        && listeners[i].promise().requeue == requeue_mode::READY) {
                    pool_task to_resume = listeners[i];
                    listeners[i] = nullptr;
                    events[i].fd = -1;

                    pool->queue(to_resume);
                    std::cout << "io resumed " << i  << " for " << events[i].revents << '\n';
                }
            }

            std::erase_if(events, [](const auto &e) { return e.fd == -1; });
            std::erase_if(listeners, [](const auto &h) { return !h; });
        }

        for (const auto &h : listeners)
            if (h) h.destroy();
    }

    pool_future notify_worker(thread_pool *pool, std::stop_token tok) {
        int pipefds[2];
        if (pipe(pipefds) == -1)
            std::cout << "err on pipe: " << strerror(errno) << '\n';
        pool->notif_fd = pipefds[1];
        auto d = defer{[a=pipefds[0],b=pipefds[1]]() {
            close(a);
            close(b);
        }};

        for (int i = 0; i < 2; i++) {
            int flags = fcntl(pipefds[i], F_GETFL);
            if (fcntl(pipefds[i], F_SETFL, flags | O_NONBLOCK) == -1)
                std::cout << "err on fcntl: " << strerror(errno) << '\n';
        }

        char buf[16];
        while (!tok.stop_requested()) {
            co_await io_awaiter{pipefds[0], POLLIN, false};
            // dont care about result, just consume it
            read(pipefds[0], buf, sizeof(buf));
        }
    }

    thread_pool::thread_pool(int num) {
        threads.reserve(num);
        threads.emplace_back(io_thread_fun, this, stop.get_token());
        for (int i = 1; i < num; i++)
            threads.emplace_back(worker_thread_fun, this, stop.get_token());
        queue(notify_worker(this, stop.get_token()));
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
