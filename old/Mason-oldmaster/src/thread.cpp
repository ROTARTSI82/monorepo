//
// Created by Grant on 2019-08-23.
//

#include "mason/thread.h"

namespace mason {
    Timer::~Timer() {
        stop();
    }

    void Timer::threadFunc(Timer *tm) {
        tm->pre();
        while (tm->running) {
            tm->tick();
        }
        tm->post();
    }

    void Timer::start() {
        thread = new std::thread(threadFunc, this);
    }

    void Timer::stop() {
        running = false;
        if (thread->joinable()) {
            thread->join();
        }
        delete thread;
    }
}
