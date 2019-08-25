//
// Created by Grant on 2019-08-24.
//

#include "mason/event.h"

namespace mason {
    std::queue<Event *> event_que;
    std::mutex event_mtx;
}

