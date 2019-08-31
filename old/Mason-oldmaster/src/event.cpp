//
// Created by Grant on 2019-08-24.
//

#include "mason/event.h"

namespace mason {
    std::queue<Event *> eventQueue;
    std::mutex eventMtx;
}

