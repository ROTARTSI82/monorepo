//
// Created by 25granty on 10/13/19.
//

#pragma once

#ifndef MASONSHARE_AL_CORE_H
#define MASONSHARE_AL_CORE_H

#include "OpenAL.h"
#include <string>

#include "mason/log.h"

namespace mason::al {
    void handle_single_error(const std::string &msg = "Unknown Error");

    void flush_errors(const std::string &msg = "Unknown Error");
}

#endif //MASONSHARE_AL_CORE_H
