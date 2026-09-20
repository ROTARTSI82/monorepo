//
// Created by 25granty on 10/14/19.
//

#include "mason/al/al_listener.h"

namespace mason::al {

    void al_listener::set_orientation(glm::vec3 forward, glm::vec3 up) {
        ALfloat ori_arr[] = {forward.x, forward.y, forward.z, up.x, up.y, up.z};
        alListenerfv(AL_ORIENTATION, ori_arr);
    }

    void al_listener::set_position(glm::vec3 pos) {
        alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
    }

    void al_listener::set_velocity(glm::vec3 vel) {
        alListener3f(AL_VELOCITY, vel.x, vel.y, vel.z);
    }
}

