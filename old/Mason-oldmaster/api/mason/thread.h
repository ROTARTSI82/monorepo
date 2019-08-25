//
// Created by Grant on 2019-08-23.
//

#pragma once

#ifndef MASON_THREAD_H
#define MASON_THREAD_H

#include "mason/masonpch.h"

namespace mason {
    class Timer {
    public:
        static void threadFunc(Timer *tm);

        std::thread *thread = nullptr;
        bool running = true;
        unsigned int delay = 0;

        explicit Timer(unsigned int wait) : delay(wait) {};

        Timer() = default;
        ~Timer();

        virtual void pre() const {};

        virtual void tick() const {};

        virtual void post() const {};

        void start();

        void stop();
    };
}

#endif //MASON_THREAD_H
