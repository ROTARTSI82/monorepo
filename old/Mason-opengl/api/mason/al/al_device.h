//
// Created by 25granty on 10/13/19.
//

#pragma once

#ifndef MASONSHARE_AL_DEVICE_H
#define MASONSHARE_AL_DEVICE_H

#include "OpenAL.h"

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

#endif //MASONSHARE_AL_DEVICE_H
