//
// Created by Grant on 2019-08-24.
//
#pragma once

#ifndef ROTARTSI_MASON_EVENT_H
#define ROTARTSI_MASON_EVENT_H

#include "mason/masonpch.h"

namespace mason {

    class Event {
    public:
        bool handled = false;
        std::string name = "Generic Event";
    };

    extern std::queue<Event *> event_que;
    extern std::mutex event_mtx;
}

#endif //MASON_EVENT_H
