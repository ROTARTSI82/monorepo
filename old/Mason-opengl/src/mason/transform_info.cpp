//
// Created by Grant on 2019-09-03.
//

#include "mason/transform_info.h"

namespace mason {

    transform_info::transform_info(glm::vec3 pos, glm::vec3 rot, glm::vec3 scale) {
        position = pos;
        rotation = rot;
        this->scale = scale;

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
