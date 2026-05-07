#include "mc/async.hpp"
#include <cassert>
#include <fcntl.h>
#include <iostream>
#include <stop_token>
#include <cstring>
#include <thread>
#include <unistd.h>

namespace mc {

    inline void cancel_thread_pool_task(const pool_task &task, thread_pool *pool) {
        task_rescheduler *ret = task.promise->return_link;
        if (ret)
            ret->return_link(task, pool);
        else
            task.destroy();
    }

    void thread_pool::worker_thread_fun(thread_pool *pool, std::stop_token stop) {
        while (!stop.stop_requested()) {
            pool_task job = nullptr;
            {
                std::unique_lock<std::mutex> lg(pool->task_mtx);
                if (pool->tasks.empty()) {
                    pool->ready.wait(lg, [&]() {
                        return stop.stop_requested() || !pool->tasks.empty();
                    });
                    if (stop.stop_requested())
                        break;
                }
                if (pool->tasks.empty())
                    continue;
                job = pool->tasks.front();
                pool->tasks.pop();
            }
            if (!job.done())
                job.resume();

            task_rescheduler *rs = job.promise->resched;
            if (rs)
                rs->reschedule(job, pool);
            else if (!job.done())
                pool->queue(job);
            else
                cancel_thread_pool_task(job, pool);
        }
    }


    void thread_pool::io_thread_fun(thread_pool *pool, std::stop_token stop) {
        std::vector<pollfd> events{};
        std::vector<pool_task> listeners{};

        while (!stop.stop_requested()) {
            for (size_t i = 0; i < events.size(); i++) {
                if (events[i].revents
                        && i < listeners.size()
                        && listeners[i]) {
                    if (events[i].fd != pool->notif_fds[0]) {
                        pool_task to_resume = listeners[i];
                        listeners[i] = nullptr;
                        events[i].fd = -1;
                        pool->queue(to_resume);
                    } else {
                        // notification worker: don't kick it back into the queue
                        // keep it in the poll set. running it on the io thread
                        // is fine since it just marks the notification as consumed
                        // and basically busy-waits.
                        listeners[i].resume();
                    }
                }
            }

            std::erase_if(events, [](const auto &e) { return e.fd == -1; });
            std::erase_if(listeners, [](const auto &h) { return !h; });

            {
                std::unique_lock<std::mutex> lg(pool->event_mtx);
                events.reserve(events.size() + pool->events.size());
                events.insert(events.end(), pool->events.begin(), pool->events.end());
                listeners.reserve(listeners.size() + pool->event_listeners.size());
                listeners.insert(listeners.end(),
                    pool->event_listeners.begin(), pool->event_listeners.end());
                pool->event_listeners.clear();
                pool->events.clear();
            }
            for (const auto &f: events)
                std::cout << '\t' << f.fd;
            std::cout << '\n';
            // todo: consider replacing with EPOLL with EPOLLONESHOT,
            // or io_uring maybe? the epoll approach is epoll_ctl with
            // EPOLL_CTL_ADD (or if EEXIST, EPOLL_CTL_MOD) and
            // with the coroutine handle in .data of epoll_event struct.
            // or we can use edge-triggered EPOLLET and never suspend
            // until EAGAIN.
            if (poll(events.data(), events.size(), 1000) == -1)
                std::cout << "err on poll: " << strerror(errno) << '\n';
        }

        // free in-flight listeners correctly
        for (size_t i = 0; i < listeners.size(); i++)
            cancel_thread_pool_task(listeners[i], pool);
    }

    pool_future<void> notify_worker(int fd, std::stop_token tok) {
        char buf[16];
        while (!tok.stop_requested()) {
            // this task will always be waiting on the notify_fd
            // so that fd is always included in our poll(), and
            // the io_thread will wake up whenever we write to notify_fd.
            co_await io_awaiter{fd, POLLIN, false};
            if (read(fd, buf, sizeof(buf)) == 0)
                break;
        }
    }

    thread_pool::thread_pool(int num) {
        if (pipe(this->notif_fds) == -1)
            std::cout << "err on pipe: " << strerror(errno) << '\n';
        for (int pipefd : notif_fds) {
            int flags = fcntl(pipefd, F_GETFL);
            if (fcntl(pipefd, F_SETFL, flags | O_NONBLOCK) == -1)
                std::cout << "err on fcntl: " << strerror(errno) << '\n';
        }

        threads.reserve(num);
        queue(notify_worker(notif_fds[0], stop.get_token()));
        for (int i = 1; i < num; i++)
            threads.emplace_back(worker_thread_fun, this, stop.get_token());
        threads.emplace_back(io_thread_fun, this, stop.get_token());
    }

    thread_pool::~thread_pool() {
        stop.request_stop();
        close(notif_fds[1]); // also serve to wake io_thread
        ready.notify_all();
        for (auto &thread : threads)
            thread.join();

        do {
            while (!tasks.empty()) {
                // this can add a task to the back of the tasks queue
                cancel_thread_pool_task(tasks.front(), this);
                tasks.pop();
            }

            for (size_t i = 0; i < event_listeners.size(); i++)
                // this may grow the vector or add to tasks
                cancel_thread_pool_task(event_listeners[i], this); 
            event_listeners.clear();
        } while (!tasks.empty() || !event_listeners.empty());

        close(notif_fds[0]);
    }
}
