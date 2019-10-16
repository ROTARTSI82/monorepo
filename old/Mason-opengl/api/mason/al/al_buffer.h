//
// Created by 25granty on 10/14/19.
//

#pragma once

#ifndef MASONSHARE_AL_BUFFER_H
#define MASONSHARE_AL_BUFFER_H

#include "OpenAL.h"
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

#endif //MASONSHARE_AL_BUFFER_H
