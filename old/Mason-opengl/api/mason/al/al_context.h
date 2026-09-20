//
// Created by 25granty on 10/13/19.
//

#pragma once

#ifndef __MASON_AL_CONTEXT_H
#define __MASON_AL_CONTEXT_H


#if defined(__APPLE__) || defined(__MACOSX)
#include "OpenAL.h"

#else
#include "AL/al.h"
#include "AL/alc.h"
#endif // !__APPLE__

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

#endif //__MASON_AL_CONTEXT_H
