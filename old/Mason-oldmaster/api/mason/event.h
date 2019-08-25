//
// Created by Grant on 2019-08-24.
//
#pragma once

#ifndef MASON_EVENT_H
#define MASON_EVENT_H

#include "mason/masonpch.h"

namespace mason {

    class Event {
    public:
        bool handled = false;
        std::string name = "Generic Event";
    };

    std::queue<Event *> event_que;
    std::mutex event_mtx;
}

#endif //MASON_EVENT_H
