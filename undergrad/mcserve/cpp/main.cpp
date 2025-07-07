#include <iostream>
#include "async.cpp"

int main() {
    next = new std::queue<std::coroutine_handle<>>;
    auto fut = lol();
    fut.frame.promise().shared_interface.self = fut.frame;
    schedule_once(fut.frame);
    while (!next->empty()) {
        std::cout << "\t task list = " << next->size() << " resuming " << (uint64_t) next->front().address() << '\n';
        next->front().resume();
        next->pop();
    }

    std::cout << "finished!\n";
    delete next;
    return 0;
}