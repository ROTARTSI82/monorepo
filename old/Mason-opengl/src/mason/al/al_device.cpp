//
// Created by 25granty on 10/13/19.
//

#include "mason/al/al_device.h"

namespace mason::al {

    al_device *default_device;

    void update_default_device() {
        delete default_device;
        default_device = new al_device(alcOpenDevice(nullptr));
    }

    al_device::al_device(ALCdevice *dev) : dev_obj(dev) {}

    al_device::al_device(const ALCchar *dev) {
        dev_obj = alcOpenDevice(dev);
    }

    al_device::~al_device() {
        alcCloseDevice(dev_obj);
    }
}
