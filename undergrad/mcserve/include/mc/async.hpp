#pragma once

#include <coroutine>
#include <condition_variable>
#include <queue>
#include <thread>
#include <mutex>
#include <stop_token>

namespace mc {
    struct pool_future {
        struct promise_type {
            struct {
                bool destroy : 1;
                uint8_t prio : 7;
            };
            std::suspend_always initial_suspend() { return {}; }
            std::suspend_always final_suspend() noexcept { return {}; }
            void return_void() {}
            void unhandled_exception() {}
            pool_future get_return_object() {
                return {std::coroutine_handle<promise_type>::from_promise(*this)};
            }
        };

        struct compare {
            bool operator()(const std::coroutine_handle<promise_type> &a,
                            const std::coroutine_handle<promise_type> &b) {
                return a.promise().prio < b.promise().prio;
            }
        };

        std::coroutine_handle<promise_type> handle = {nullptr};
        pool_future(const std::coroutine_handle<promise_type> &in) : handle(in) {}
        ~pool_future() { }

        [[nodiscard]] inline bool done() { return handle.done(); }
        inline void resume() { handle.resume(); }
    };

    using pool_handle = std::coroutine_handle<pool_future::promise_type>;

    // awaitable object for setting prio
    struct suspend_and_set_prio {
        int prio;
        suspend_and_set_prio(int p) : prio(p) {}
        bool await_ready() { return false; }
        void await_resume() { }
        void await_suspend(pool_handle handle) {
            handle.promise().prio = prio;
        }
    };

    class thread_pool {
    public:
        std::vector<std::thread> threads{};
        std::priority_queue<pool_handle, std::vector<pool_handle>, pool_future::compare> tasks{};
        std::condition_variable ready{};
        std::mutex mtx{};
        std::stop_source stop{};

        thread_pool(int num);
        ~thread_pool();

        static void worker_thread_fun(thread_pool *, std::stop_token);

        inline void raw_queue(const pool_handle &coro) {
            {
                std::unique_lock<std::mutex> lg(mtx);
                tasks.emplace(coro);
            }
            ready.notify_one();
        }

        inline void queue(const pool_handle &coro,
                          int prio = 1, bool destroy = true) {
            coro.promise().prio = prio;
            coro.promise().destroy = destroy;
            raw_queue(coro);
        }
    };
}
