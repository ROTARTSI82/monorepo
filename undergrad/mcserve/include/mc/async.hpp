#pragma once

#include <coroutine>
#include <condition_variable>
#include <optional>
#include <queue>
#include <thread>
#include <mutex>
#include <stop_token>


#define CO_AWAIT(promise) { \
    auto _p = (promise); \
    while (!_p.done()) { \
        co_await std::suspend_always{}; \
        _p.resume(); \
    } \
    _p \
}

namespace mc {

    template <typename T>
    struct returning_promise;


    struct void_future {
        struct promise_type {
            std::suspend_always initial_suspend() { return {}; }
            std::suspend_always final_suspend() noexcept { return {}; }
            void return_void() {}
            void unhandled_exception() {}
            void_future get_return_object() {
                return {std::coroutine_handle<promise_type>::from_promise(*this)};
            }
        };

        std::coroutine_handle<promise_type> handle = {nullptr};
        void_future(const std::coroutine_handle<promise_type> &in) : handle(in) {}
        ~void_future() { handle.destroy(); }

        [[nodiscard]] inline bool done() { return handle.done(); }
        inline void resume() { handle.resume(); }
    };

    template <typename T, typename P = returning_promise<T>>
    struct future {
        using promise_type = P;

        std::coroutine_handle<P> handle = {nullptr};
        future(const std::coroutine_handle<P> &in) : handle(in) {}

        [[nodiscard]] inline bool done() { return handle.done(); }
        [[nodiscard]] inline P &promise() { return handle.promise(); }
        inline void resume() { handle.resume(); }

        [[nodiscard]] constexpr inline T &value() {
            return handle.promise().value.value();
        }

        ~future() { handle.destroy(); }
    };


    template <typename T>
    struct returning_promise {
        std::optional<T> value;

        future<T, returning_promise<T>> get_return_object() {
            return {std::coroutine_handle<returning_promise<T>>::from_promise(*this)};
        }
        void return_value(T &&v) { value = v; }
    };

    template <typename T>
    struct noalloc_promise : returning_promise<T> {
        void *operator new(size_t size) {
            std::cout << "op new: " << size << '\n';
            return ::operator new(size);
        };
        void *operator new[](size_t size) {
            std::cout << "op new[]: " << size << '\n';
            return ::operator new[](size);
        };

        future<T, noalloc_promise<T>> get_return_object() {
            return {std::coroutine_handle<noalloc_promise<T>>::from_promise(*this)};
        }
    };

    class thread_pool {
    public:
        struct task {
            std::coroutine_handle<> coro;
            int prio;

            [[nodiscard]] inline bool operator<(const task &other) const noexcept {
                return prio < other.prio;
            }
        };

        std::vector<std::thread> threads{};
        std::priority_queue<task> tasks{};
        std::condition_variable ready{};
        std::mutex mtx{};
        std::stop_source stop{};

        thread_pool(int num);
        ~thread_pool();

        inline void queue(const std::coroutine_handle<> &coro, int prio) {
            tasks.emplace(coro, prio);
        }
    };
}
