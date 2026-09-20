//
// Created by 25granty on 10/14/19.
//

#pragma once

#ifndef __MASON_AL_LISTENER_H
#define __MASON_AL_LISTENER_H

#if defined(__APPLE__) || defined(__MACOSX)
#include "OpenAL.h"

#else
#include "AL/al.h"
#include "AL/alc.h"
#endif // !__APPLE__

#include "glm/glm.hpp"

namespace mason::al {
    class al_listener {
    public:
        static void set_orientation(glm::vec3 forward, glm::vec3 up);

        static void set_position(glm::vec3 pos);

        static void set_velocity(glm::vec3 vel);
    };
}

#endif //__MASON_AL_LISTENER_H
