//
// Created by Grant on 10/1/19.
//

#pragma once

#ifndef MASONSHARE_RIGIDBODY_H
#define MASONSHARE_RIGIDBODY_H

#include "mason/transform_info.h"

namespace mason {
    class rigidbody {
    public:
        mason::transform_info transforms;
        glm::vec3 velocity;
        glm::vec3 acceleration;
        float mass;

        explicit rigidbody(mason::transform_info trans = transform_info(), glm::vec3 vel = glm::vec3(0),
                           glm::vec3 accel = glm::vec3(0), float mass = 1);

        virtual ~rigidbody() = default;

        void clear_acceleration();

        void push_force(glm::vec3 force);

        void apply_friction(float coefficient);

        void apply_acceleration();
    };
}


#endif //MASONSHARE_RIGIDBODY_H
