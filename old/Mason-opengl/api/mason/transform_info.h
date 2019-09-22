//
// Created by Grant on 2019-09-03.
//

#pragma once

#ifndef MASON_MASON_H
#define MASON_MASON_H

#include <chrono>

#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"

namespace mason {

    class transform_info {
    private:
        glm::vec3 last_position;
        std::chrono::high_resolution_clock::time_point last_update;
    public:
        glm::vec3 position;
        glm::vec3 rotation;
        glm::vec3 scale;

        glm::vec3 velocity;

        glm::mat4 transforms;
        glm::vec3 up, right, forward;

        explicit transform_info(glm::vec3 pos = glm::vec3(0), glm::vec3 rot = glm::vec3(0), glm::vec3 scale = glm::vec3(1));

        void update_velocity();

        void update_transforms();
    };
}

#endif //MASON_MASON_H
