//
// Created by 25granty on 10/12/19.
//

#pragma once

#ifndef __MASON_TASK_POOL_H
#define __MASON_TASK_POOL_H

#include <vector>
#include <future>
#include <queue>

namespace mason {

    class task_pool;

    struct __MASON_TASK_POOL_TASK {
        void *(*func)(unsigned int, task_pool *, void *);

        void *data;
    };

    class task_pool {
    public:
        std::vector<__MASON_TASK_POOL_TASK> tasks;

        std::queue<std::thread *> active_threads;

        std::queue<std::future<void *>> futures;

        volatile bool is_running = false;

        task_pool() = default;

        ~task_pool();

        void push_task(void *(*task)(unsigned int, task_pool *, void *), void *usr_dat);

        void start_tasks();

        void stop_tasks();
    };
}


#endif //__MASON_TASK_POOL_H
