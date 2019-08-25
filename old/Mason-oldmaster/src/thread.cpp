//
// Created by Grant on 2019-08-23.
//

#include "mason/thread.h"

namespace mason {
    Timer::~Timer() {
        stop();
    }

    void Timer::threadFunc(Timer *tm) {
        while (tm->running) {
            tm->tick();
            std::this_thread::sleep_for(
                    std::chrono::milliseconds(static_cast<std::chrono::milliseconds::rep>(tm->delay)));
        }
    }

    void Timer::start() {
        running = true;
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
