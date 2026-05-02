#pragma once

#include <coroutine>
#include <condition_variable>
#include <queue>
#include <thread>
#include <mutex>
#include <iostream>
#include <stop_token>

#include <poll.h>
#include <unistd.h>

namespace mc {
    class thread_pool;

    enum class task_state : int {
        QUEUED, RUNNING, IO_BLOCKED
    };

    struct pool_future {

        struct promise_type {
            thread_pool *owner = nullptr;
            std::atomic<task_state> state = task_state::QUEUED;

            std::suspend_always initial_suspend() { return {}; }
            std::suspend_always final_suspend() noexcept { return {}; }
            void return_void() {}
            void unhandled_exception() {}
            pool_future get_return_object() {
                return {std::coroutine_handle<promise_type>::from_promise(*this)};
            }
        };

        std::coroutine_handle<promise_type> handle = {nullptr};
        pool_future(const std::coroutine_handle<promise_type> &in) : handle(in) {}
        ~pool_future() { if (handle) handle.destroy(); }

        [[nodiscard]] inline bool done() { return handle.done(); }
        inline void resume() { handle.resume(); }
    };

    using pool_task = std::coroutine_handle<pool_future::promise_type>;

    class thread_pool {
    public:
        std::vector<std::thread> threads{};
        std::queue<pool_task> tasks{};
        std::mutex task_mtx{};

        std::condition_variable ready{};
        std::stop_source stop{};

        // vectors of the same length: when the pollfd event fires,
        // we queue up the corresponding event in event_listeners
        std::vector<pollfd> events{};
        std::vector<pool_task> event_listeners{};
        std::mutex event_mtx{};
        int notif_fd = STDOUT_FILENO;

        thread_pool(int num);
        ~thread_pool();

        static void worker_thread_fun(thread_pool *, std::stop_token);
        static void io_thread_fun(thread_pool *, std::stop_token);

        inline void queue(const pool_task &coro) {
            coro.promise().owner = this;
            {
                std::unique_lock<std::mutex> lg(task_mtx);
                coro.promise().state = task_state::QUEUED;
                tasks.emplace(coro);
            }
            ready.notify_one();

        }

        inline void queue(pool_future &&fut) {
            queue(fut.handle);
            fut.handle = nullptr;
        }
    };

    // hack to avoid having to heap allocate each time we want to do an async io op
    // intended to be used as `while (co_await xx != -1);` to repeatedly retry.
    // mental model for a function that returns io_awaiter:
    //   for the failure case execution jumps back to the start of the function
    //   to retry it again.
    struct io_awaiter {
        ssize_t bytes;
        int fd;
        short events;
        bool suspended, notify;

        io_awaiter(int fd, short events, bool notify = true) :
            fd(fd), events(events), suspended(true), notify(notify) {
            if (fd == -1) {
                std::cout << "err: io_await on fd=-1\n";
                suspended = false;
            }
        };

        io_awaiter(ssize_t bytes) :
            bytes(bytes), suspended(false) {};

        inline bool await_ready() { return !suspended; }
        void await_suspend(pool_task h) {
            std::cout << "suspend io_awaiter fd=" << fd << '\n';
            thread_pool *pool = h.promise().owner;
            {
                std::unique_lock<std::mutex> lg(pool->event_mtx);
                h.promise().state = task_state::IO_BLOCKED;
                pool->event_listeners.emplace_back(h);
                pool->events.emplace_back(fd, events, 0); // pollfd
            }
            int yes = 1;
            if (notify)
                write(pool->notif_fd, &yes, sizeof(yes));
        }
        // value of the co_await expression: signal we should retry if we suspended.
        ssize_t await_resume() { return suspended ? -1 : bytes; };
    };
}
