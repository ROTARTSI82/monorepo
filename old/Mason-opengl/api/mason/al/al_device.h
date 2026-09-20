//
// Created by 25granty on 10/13/19.
//

#pragma once

#ifndef __MASON_AL_DEVICE_H
#define __MASON_AL_DEVICE_H

#if defined(__APPLE__) || defined(__MACOSX)
#include "OpenAL.h"

#else
#include "AL/al.h"
#include "AL/alc.h"
#endif // !__APPLE__

namespace mason::al {
    class al_device {
    public:
        ALCdevice *dev_obj;

        explicit al_device(ALCdevice *dev);

        explicit al_device(const ALCchar *dev);

        ~al_device();
    };

    extern al_device *default_device;

    void update_default_device();
}

#endif //__MASON_AL_DEVICE_H
