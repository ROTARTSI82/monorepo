//
// Created by 25granty on 10/13/19.
//

#pragma once

#ifndef MASONSHARE_AL_CONTEXT_H
#define MASONSHARE_AL_CONTEXT_H

#include "OpenAL.h"
#include "mason/al/al_device.h"

namespace mason::al {
    class al_context {
    public:
        ALCcontext *context_obj;

        explicit al_context(ALCdevice *dev = nullptr, ALCint *attr = nullptr);

        explicit al_context(ALCcontext *cont);

        ~al_context();

        void bind();
    };
}

#endif //MASONSHARE_AL_CONTEXT_H
