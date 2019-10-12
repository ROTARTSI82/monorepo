//
// Created by 25granty on 10/12/19.
//

#pragma once

#ifndef MASONSHARE_THREAD_POOL_H
#define MASONSHARE_THREAD_POOL_H

#include <queue>
#include <vector>
#include <thread>
#include <future>

namespace mason {
    class thread_pool {
    private:
        static void worker_func(thread_pool *parent);

    public:
        constexpr static double thread_mult = 1.0;

        std::vector<std::thread *> workers;
        std::queue<std::packaged_task<void(thread_pool *)> *> tasks;
        std::mutex task_mutex;

        volatile bool is_running = false;

        thread_pool() = default;

        ~thread_pool();

        void push_task(std::packaged_task<void(thread_pool *)> *task);

        void add_worker();
//
//        // This will block until the task the worker is executing is finished.
//        void remove_worker();

        void start(unsigned limit = 0);

        void stop();
    };
}


#endif //MASONSHARE_THREAD_POOL_H
