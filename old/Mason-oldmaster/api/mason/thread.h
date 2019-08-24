//
// Created by Grant on 2019-08-23.
//

#pragma once

#ifndef MASON_THREAD_H
#define MASON_THREAD_H

#include <thread>
#include <iostream>

namespace mason {
    class Timer {
    public:
        static void threadFunc(Timer *tm);

        std::thread *thread = nullptr;
        bool running = true;

        ~Timer();

        void pre() const {};

        void tick() const {};

        void post() const {};

        void start();

        void stop();
    };
}

#endif //MASON_THREAD_H
