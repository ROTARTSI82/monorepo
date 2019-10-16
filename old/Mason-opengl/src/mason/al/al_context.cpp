//
// Created by 25granty on 10/13/19.
//

#include "mason/al/al_context.h"

namespace mason::al {

    al_context::al_context(ALCcontext *cont) : context_obj(cont) {}

    al_context::al_context(ALCdevice *dev, ALCint *attr) {
        context_obj = alcCreateContext(dev == nullptr ? default_device->dev_obj : dev, attr);
    }

    al_context::~al_context() {
        alcMakeContextCurrent(nullptr);
        alcDestroyContext(context_obj);
    }

    void al_context::bind() {
        alcMakeContextCurrent(context_obj);
    }
}
