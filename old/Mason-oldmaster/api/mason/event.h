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
    };

    extern std::queue<Event *> eventQueue;
    extern std::mutex eventMtx;
}

#endif //MASON_EVENT_H
