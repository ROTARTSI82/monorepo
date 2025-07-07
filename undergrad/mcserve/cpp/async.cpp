#include <coroutine>
#include <cstdint>
#include <iostream>
#include <queue>
#include <variant>
#include <ostream>

std::queue<std::coroutine_handle<>> *next;

inline void schedule_once(std::coroutine_handle<> handle) {
    std::cout << "sched next " << (uint64_t) handle.address() << '\n';
    next->push(handle);
}

// function to schedule a group of them

struct unit {};
std::ostream &operator<<(std::ostream &os, const unit &) { return os << "()"; }

template <typename... T>
std::ostream &operator<<(std::ostream &os, const std::variant<T...> &var) { return os << "variant(??)"; }

struct suspend_always {
    constexpr bool await_ready() noexcept { return false; }
    void await_resume() noexcept {}
    void await_suspend(std::coroutine_handle<> parent) noexcept { schedule_once(parent); }
};

struct shared_interface_t {
    std::coroutine_handle<> self = std::noop_coroutine();
    shared_interface_t *link = nullptr;
    shared_interface_t *child = nullptr;
    bool active = true;

    void cancel() {
        std::cout << "passing cancel " << (uint64_t) this << " -> " << (uint64_t) child << '\n';
        active = false;
        if (child)
            child->cancel();
    }

    template <typename B>
    inline void link_as_subroutine_to(const std::coroutine_handle<B> ret_to) {
        std::cout << "set link ->" << (uint64_t) ret_to.address() << '\n';
        if (ret_to.promise().shared_interface.self != std::noop_coroutine()
                && ret_to != ret_to.promise().shared_interface.self)
            std::cout << "wtf: link_as_subroutine_to is overriding a link?\n";
        ret_to.promise().shared_interface.self = ret_to;
        ret_to.promise().shared_interface.child = this;
        link = &ret_to.promise().shared_interface;
    }
};

template <typename T, typename F>
struct linking_promise {
    // return address: where should we go when this promise fills?
    std::optional<T> value = std::nullopt;
    shared_interface_t shared_interface = {};

    F get_return_object() {
        return F(std::coroutine_handle<linking_promise>::from_promise(*this));
    }

    // intentionally the std:: version and not the custom one that schedules:
    // this is awaited when we build our futures, so "returning to the caller" doesn't
    // mean returning all the way to the top level loop.
    inline std::suspend_always initial_suspend() { return {}; }
    void return_value(T &&val) {
        std::cout << "RET " << val << '\n';
        value = val; }
    void unhandled_exception() { /* DONT USE EXCEPTIONS BRO */ }

    struct continuation_handler {
        shared_interface_t *self = nullptr;
        explicit continuation_handler(shared_interface_t *self) : self(self) {}

        constexpr bool await_ready() noexcept { return false; }
        void await_resume() noexcept {}

        // this object should only ever be used with coroutines of this promise_type.
        std::coroutine_handle<> await_suspend(std::coroutine_handle<linking_promise> dying) noexcept {
            // be careful here: modifying `dying` will modify `this`!
            // especially pay attention to freeing memory and multithreading.

            if (!dying.done())
                std::cout << "wtf: continuation_handler trying to continue a non-done coroutine?\n";
            if (self->link && self->link->child && self->link->child != self)
                std::cout << "wtf: unlinking something that was never linked (this might be the top level of select() or join()): "
                    << (uint64_t) self->link->child << '\t' << (uint64_t) self << '\n';

            // possibility that link is dead because of something like join() or select()
            // where we have multiple handles linking back to the same master.
            // this is probably not the correct way to handle this lol
            if (self->link)
                self->link->child = nullptr;

            if (self->link && self->link->self && !self->link->self.done())
                if (self->link->active && self->active)
                    return self->link->self;
                else
                    std::cout << "LINK INACTIVE\n";
            else
                std::cout << "LINK DEAD\n";
            return std::noop_coroutine();
        }
    };

    inline continuation_handler final_suspend() noexcept { return continuation_handler{&shared_interface}; }
};

template <typename T>
struct future {
    using promise_type = linking_promise<T, future<T>>;
    using handle_t = std::coroutine_handle<promise_type>;

    handle_t frame;

    explicit future(handle_t &&frame) : frame(frame) {}
    constexpr bool await_ready() { return false; }

    template <typename B>
    std::coroutine_handle<> await_suspend(std::coroutine_handle<B> parent) {
        // when we get co_awaited, our `frame` is the subtask:
        // we need to schedule completing the subtask, then fire `parent` after subtask is done.
        frame.promise().shared_interface.link_as_subroutine_to(parent);

        if (frame.promise().shared_interface.active && parent.promise().shared_interface.active)
            return frame;
        else
            return std::noop_coroutine();
    }

    T await_resume() {
        if (!frame.done() || !frame.promise().value.has_value())
            std::cout << "wtf: caller resumed before callee finished\n";
        return frame.promise().value.value();
    }
};

constexpr inline int count_args() { return 0; }
template <typename T, typename... Args>
constexpr inline int count_args(const T &first, const Args &... rest) {
    return 1 + count_args(rest...);
}

// barrier that gives a coroutine that must be resume()'d a certain number of times
// before its parent is resumed.
// NOTE: intentionally using std:: instead of custom suspend_always
// to avoid queueing this task into the list automatically.
// the point is to use this as a goto label basically, manually linking things to it
inline future<unit> resume_barrier(int resumes_before_finish) {
    // todo: should resumes_before_finish be a template param?
    for (int i = 0; i < resumes_before_finish; i++)
        co_await std::suspend_always{};
    co_return {};
}

constexpr inline void _link_all(future<unit> &) {} // base case for recursion
template <typename T, typename... Args>
constexpr inline void _link_all(future<unit> &parent, future<T> first, future<Args>... rest) {
    first.frame.promise().shared_interface.link_as_subroutine_to(parent.frame);
    schedule_once(first.frame);
    _link_all(parent, rest...);
}

template <typename V>
constexpr inline void _select_check_futs(V &variant) {} // base case
template <typename V, typename T, typename... Args>
constexpr inline void _select_check_futs(V &variant, future<T> first, future<Args>... rest) {
    if (first.frame.done() && first.frame.promise().value.has_value()) {
        std::cout << "has val select: " << first.frame.promise().value.value() << '\n';
        variant = first.frame.promise().value.value();
    } else {
        std::cout << "no val select\n";
        first.frame.promise().shared_interface.cancel();
    }

    _select_check_futs(variant, rest...);
}

template <typename... Args>
inline future<std::variant<Args...>> select(future<Args>... futures) {
    auto nop = resume_barrier(1);
    _link_all(nop, futures...);
    co_await nop;

    std::variant<Args...> ret = {};
    _select_check_futs(ret, futures...);
    co_return ret;
}

template <typename... Args>
inline future<std::tuple<Args...>> join(future<Args>... futures) {
    auto nop = resume_barrier(count_args(futures...));
    _link_all(nop, futures...);
    co_await nop;
    co_return std::make_tuple(futures.frame.promise().value.value()...);
}


future<std::string> count(int max) {
    for (int i = 0; i < max; i++) {
        std::cout << i << '\n';
        co_await suspend_always{};
    }

    co_return "<LMAO COUNT WAS HERE>";
}

future<int> fib(int n) {
    co_await suspend_always{};

    std::cout << "FIB " << n << '\n';

    if (n <= 1) co_return 1;
    int left = co_await fib(n-1);
    int right = co_await fib(n-2);
    std::cout << "RET FIB " << n << " = " << left << " + " << right << '\n';
    co_return left + right;
}


future<unit> lol() {
    auto test = co_await select(count(10), fib(10));

    if (test.index() == 0)
        std::cout << "count = " << std::get<0>(test) << '\n';
    else
        std::cout << "fib = " << std::get<1>(test) << '\n';
    co_return {};
}


