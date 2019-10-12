//
// Created by 25granty on 10/12/19.
//

#include <mason/log.h>
#include "mason/thread_pool.h"

namespace mason {

    thread_pool::~thread_pool() {
        stop();
    }

    void thread_pool::push_task(std::packaged_task<void(thread_pool *)> *task) {
        std::lock_guard<std::mutex> lock(task_mutex); // Being exception safe, cause why not?
        tasks.push(task);
    }

    void thread_pool::start(unsigned int limit) {
        if (is_running) {
            MASON_WARN("thread_pool::start() called when thread_pool already running! Ignoring invocation!");
            return;
        }

        is_running = true;
        limit = limit == 0 ? (int) (std::thread::hardware_concurrency() * thread_mult)
                           : limit;  // TODO: Find the omptimal multiplier
        limit = limit == 0 ? 8 : limit; // Default to 8 concurrent threads

        for (unsigned i = 0; i < limit; i++) {
            workers.emplace_back(new std::thread(worker_func, this));
        }
    }

    void thread_pool::stop() {
        is_running = false;

        for (auto t : workers) {
            if (t->joinable()) {
                t->join();
            } else {
                t->detach();
            }

            delete t;
        }
    }

    void thread_pool::worker_func(thread_pool *parent) {
        while (parent->is_running) {
            parent->task_mutex.lock();
            if (!parent->tasks.empty()) {
                auto current_task = parent->tasks.front();
                parent->tasks.pop();
                parent->task_mutex.unlock();

                (*current_task)(parent);
            } else {
                parent->task_mutex.unlock();
            }
        }
    }

    void thread_pool::add_worker() {
        workers.emplace_back(new std::thread(worker_func, this));
    }
}
