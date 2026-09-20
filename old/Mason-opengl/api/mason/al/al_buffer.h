//
// Created by 25granty on 10/14/19.
//

#pragma once

#ifndef __MASON_AL_BUFFER_H
#define __MASON_AL_BUFFER_H


#if defined(__APPLE__) || defined(__MACOSX)
#include "OpenAL.h"

#else
#include "AL/al.h"
#include "AL/alc.h"
#endif // !__APPLE__

#include <string>

#include "mason/log.h"

namespace mason::al {
    class al_buffer {
    public:
        ALuint buf = 0;

        explicit al_buffer(const std::string &path);

        ~al_buffer();
    };
}

#endif //__MASON_AL_BUFFER_H
