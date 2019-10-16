//
// Created by 25granty on 10/14/19.
//

#pragma once

#ifndef MASONSHARE_AL_LISTENER_H
#define MASONSHARE_AL_LISTENER_H

#include "glm/glm.hpp"
#include "OpenAL.h"

namespace mason::al {
    class al_listener {
    public:
        static void set_orientation(glm::vec3 forward, glm::vec3 up);

        static void set_position(glm::vec3 pos);

        static void set_velocity(glm::vec3 vel);
    };
}

#endif //MASONSHARE_AL_LISTENER_H
