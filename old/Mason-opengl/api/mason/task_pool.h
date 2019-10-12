//
// Created by 25granty on 10/12/19.
//

#pragma once

#ifndef MASONSHARE_TASK_POOL_H
#define MASONSHARE_TASK_POOL_H

#include <vector>
#include <future>
#include <queue>

namespace mason {
    class task_pool {
    public:
        std::vector<void (*)(unsigned int, task_pool *)> tasks;
        std::queue<std::thread *> active_threads;

        volatile bool is_running = false;

        task_pool() = default;

        ~task_pool();

        void push_task(void(*task)(unsigned int, task_pool *));

        void start_tasks();

        void stop_tasks();
    };
}


#endif //MASONSHARE_TASK_POOL_H
