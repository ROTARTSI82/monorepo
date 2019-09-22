//
// Created by Grant on 2019-09-03.
//

#include "mason/transform_info.h"

namespace mason {

    void transform_info::update_velocity() {
        auto now = std::chrono::high_resolution_clock::now();
        std::chrono::duration<float, std::milli> diff = now - last_update;
        last_update = now;

        glm::vec3 displacement = position - last_position;
        last_position = glm::vec3(position);  // Copy constructor

        velocity = displacement / (diff.count() / 1000.0f);  // Calculate velocity in units per second
    }

    transform_info::transform_info(glm::vec3 pos, glm::vec3 rot, glm::vec3 scale) {
        position = pos;
        rotation = rot;
        this->scale = scale;

        last_position = glm::vec3(position);
        last_update = std::chrono::high_resolution_clock::now();

        velocity = glm::vec3(0);

        update_transforms();
    }

    void transform_info::update_transforms() {
        transforms = glm::rotate(glm::mat4(1), 1.0f, rotation);

        up = transforms * glm::vec4(0, 1, 0, 0);
        right = transforms * glm::vec4(1, 0, 0, 0);
        forward = transforms * glm::vec4(0, 0, 1, 0);

        transforms = glm::scale(glm::mat4(1), scale) * transforms;
        transforms = glm::translate(glm::mat4(1), position) * transforms;
    }
}
