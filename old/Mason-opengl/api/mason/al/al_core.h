//
// Created by 25granty on 10/13/19.
//

#pragma once

#ifndef __MASON_AL_CORE_H
#define __MASON_AL_CORE_H

#if defined(__APPLE__) || defined(__MACOSX)
#include "OpenAL.h"

#else
#include "AL/al.h"
#include "AL/alc.h"
#endif // !__APPLE__

#include <string>

#include "mason/log.h"

namespace mason::al {
    void handle_single_error(const std::string &msg = "Unknown Error");

    void flush_errors(const std::string &msg = "Unknown Error");

    mason::al::al_context *init_al_default();
}

#endif //__MASON_AL_CORE_H
