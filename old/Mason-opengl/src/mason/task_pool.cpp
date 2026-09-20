//
// Created by 25granty on 10/12/19.
//

#include "mason/task_pool.h"
#include "mason/log.h"

namespace mason {

    task_pool::~task_pool() {
        stop_tasks();
    }

    void task_pool::push_task(void *(*task)(unsigned int, task_pool *, void *), void *usr_dat) {
        __MASON_TASK_POOL_TASK task_wrapper = {};
        task_wrapper.func = task;
        task_wrapper.data = usr_dat;

        tasks.emplace_back(task_wrapper);
    }

    void task_pool::start_tasks() {
        if (is_running) {
            MASON_WARN("task_pool::start_tasks() called when task_pool is already running! Ignoring invocation...");
            return;
        }

        is_running = true;
        for (unsigned i = 0; i < tasks.size(); i++) {
            auto current_task = tasks.at(i);
            std::packaged_task<void *(unsigned int, task_pool *, void *)> tsk(*current_task.func);

            futures.push(tsk.get_future());

            active_threads.push(new std::thread(std::move(tsk), i, this, current_task.data));
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
