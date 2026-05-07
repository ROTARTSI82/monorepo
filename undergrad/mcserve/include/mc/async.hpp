#pragma once

#include <coroutine>
#include <condition_variable>
#include <cstddef>
#include <queue>
#include <optional>
#include <cassert>
#include <thread>
#include <mutex>
#include <iostream>
#include <stop_token>

#include <poll.h>
#include <unistd.h>

namespace mc {
    class thread_pool;

    template <typename T>
    struct pool_future;

    struct task_rescheduler;
    struct unit {};

    template <typename P>
    struct defer {
        P fun;
        ~defer() { fun(); }
    };

    struct pool_promise_generic {
        thread_pool *owner = nullptr;
        task_rescheduler *resched = nullptr;
        task_rescheduler *return_link = nullptr;
    };

    template <typename T>
    struct pool_promise : pool_promise_generic {
        std::optional<T> ret;

        std::suspend_always initial_suspend() { return {}; }
        std::suspend_always final_suspend() noexcept { return {}; }
        void return_value(T &&v) { ret = std::move(v); }
        void unhandled_exception() {}
        pool_future<T> get_return_object();
    };

    template <>
    struct pool_promise<void> : pool_promise_generic {
        std::suspend_always initial_suspend() { return {}; }
        std::suspend_always final_suspend() noexcept { return {}; }
        void return_void() {}
        void unhandled_exception() {}
        pool_future<void> get_return_object();
    };

    template <typename T>
    struct pool_future {
        using promise_type = pool_promise<T>;

        std::coroutine_handle<promise_type> handle = {nullptr};
        
        pool_future(const std::coroutine_handle<promise_type> &in) : handle(in) {}

        pool_future(pool_future &) = delete;
        pool_future &operator=(pool_future &) = delete;
        
        ~pool_future() { if (handle) handle.destroy(); }
    };

    struct pool_task {
        std::coroutine_handle<> handle = nullptr;
        pool_promise_generic *promise = nullptr;

        pool_task(std::nullptr_t) : handle(nullptr), promise(nullptr) {}

        template <typename P>
        pool_task(std::coroutine_handle<P> h) : handle(h), promise(&h.promise()) {}        
        
        
        [[nodiscard]] inline bool done() const { return handle.done(); }
        inline void resume() const { handle.resume(); }
        inline void destroy() const { handle.destroy(); }
        inline operator bool() const {
            return handle && promise;
        }
    };

    template <typename T>
    inline pool_future<T> pool_promise<T>::get_return_object() {
        return {std::coroutine_handle<pool_promise>::from_promise(*this)};
    }

    inline pool_future<void> pool_promise<void>::get_return_object() {
        return {std::coroutine_handle<pool_promise>::from_promise(*this)};
    }

    struct task_rescheduler {
    public:
        // reschedule should NEVER .destroy() the coroutine, only
        // ever put it back into the pool in some way.
        // this is so that the return links are always handled correctly
        virtual void reschedule(const pool_task &h, thread_pool *pool) = 0;
        virtual void return_link(const pool_task &, thread_pool *) {}

        template <typename P>
        void await_suspend(std::coroutine_handle<P> h) {
            // we live on the coroutine frame, so using a pointer to this
            // is safe. see code in thread_pool::io_thread_fun for handling.
            h.promise().resched = this;
        }
    };

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

        // if we try to notify before setting up the notification task,
        // we harmlessly write to stdout (intended behavior).
        int notif_fds[2];

        thread_pool(int num);
        ~thread_pool();

        static void worker_thread_fun(thread_pool *, std::stop_token);
        static void io_thread_fun(thread_pool *, std::stop_token);

        inline void queue(const pool_task &task) {
            task.promise->owner = this;            
            {
                std::unique_lock<std::mutex> lg(task_mtx);
                tasks.emplace(task);
            }
            ready.notify_one();
        }

        template <typename T>
        inline void queue(pool_future<T> &&fut) {
            queue(pool_task{fut.handle});
            fut.handle = nullptr;
        }
    };

    // hack to avoid having to heap allocate each time we want to do an async io op
    // intended to be used as `while (co_await xx != -1);` to repeatedly retry.
    // mental model for a function that returns io_awaiter:
    //   for the failure case execution jumps back to the start of the function
    //   to retry it again.
    struct io_awaiter : public task_rescheduler {
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
        // value of the co_await expression: signal we should retry if we suspended.
        ssize_t await_resume() { return suspended ? -1 : bytes; };

        void reschedule(const pool_task &job, thread_pool *pool) override {
            job.promise->resched = nullptr;
            bool notify_cpy = notify;
            {
                std::unique_lock<std::mutex> lg(pool->event_mtx);
                pool->event_listeners.emplace_back(job);
                pool->events.emplace_back(fd, events, 0); // pollfd
            }
            // NOTE: After we put it back into the queue,
            // assume that `this` is possibly destroyed!
            // another thread could have resumed this coroutine
            // while reschedule() is still running.
            int yes = 1;
            if (notify_cpy)
                write(pool->notif_fds[1], &yes, sizeof(yes));
        }
    };

    template <typename T>
    struct pool_awaiter : task_rescheduler {
        std::coroutine_handle<pool_promise<T>> callee = nullptr;
        pool_task caller = nullptr;

        pool_awaiter(const pool_future<T> &fut) : callee(fut.handle) {
            // keep future alive, so it still auto-destroys the thingy
        }

        ~pool_awaiter() {
            // only the caller is owned by us.
            // the callee is owned by a pool_future on the caller frame.
            if (caller.handle != nullptr)
                caller.handle.destroy();
        }
        
        void reschedule(const pool_task &in, thread_pool *pool) override {
            // at this point, we now own the caller and are responsible for freeing
            caller = in; 
            caller.promise->resched = nullptr;
            callee.promise().return_link = this;
            pool->queue(pool_task{callee});
        }

        void return_link(const pool_task &in, thread_pool *pool) override {
            assert(in.handle == callee);
            pool_task cpy = caller;
            caller.handle = nullptr;
            pool->queue(cpy);
            // at this point, `this` can be destroyed (if we have been resumed)
            // NOTE: do not destroy the callee handle!
            // let future<T>::~future() that lives on the caller frame
            // take care of it. 
        }

        inline bool await_ready() { return false; }
        T &&await_resume() {
            return std::move(*(callee.promise().ret));
        }
    };

    template <typename T>
    pool_awaiter<T> operator co_await(const pool_future<T> &fut) {
        return {fut};
    }
}
