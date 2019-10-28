//
// Created by 25granty on 10/13/19.
//

#pragma once

#ifndef __MASON_AL_SOURCE_H
#define __MASON_AL_SOURCE_H

#include "glm/glm.hpp"
#include "mason/al/al_buffer.h" // Get OpenAL from al_buffer.h

namespace mason::al {
    class al_source {
    public:
        ALuint src = 0;

        al_source();

        ~al_source();

        void play();

        void stop();

        void pause();

        bool is_playing();

        void bind_buffer(mason::al::al_buffer *buf);

        void set_pitch(float pitch);

        void set_gain(float gain);

        void set_position(glm::vec3 pos);

        void set_velocity(glm::vec3 vel);

        void set_looping(bool looping);
    };
}

#endif //__MASON_AL_SOURCE_H
