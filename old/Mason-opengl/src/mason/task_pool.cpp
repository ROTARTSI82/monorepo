//
// Created by 25granty on 10/12/19.
//

#include "mason/task_pool.h"
#include "mason/log.h"

namespace mason {

    task_pool::~task_pool() {
        stop_tasks();
    }

    void task_pool::push_task(void(*task)(unsigned int, task_pool *)) {
        tasks.emplace_back(task);
    }

    void task_pool::start_tasks() {
        if (is_running) {
            MASON_WARN("task_pool::start_tasks() called when task_pool is already running! Ignoring invocation...");
            return;
        }

        is_running = true;
        for (unsigned long i = 0; i < tasks.size(); i++) {
            active_threads.push(new std::thread(tasks.at(i), i, this));
        }
    }

    void task_pool::stop_tasks() {
        is_running = false;
        while (!active_threads.empty()) {
            std::thread *t = active_threads.front();
            active_threads.pop();

            if (t->joinable()) {
                t->join();
            } else {
                t->detach();
            }
            delete t;
        }
    }
}
