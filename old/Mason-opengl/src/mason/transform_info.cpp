//
// Created by Grant on 2019-09-03.
//

#include "mason/transform_info.h"

namespace mason {

    transform_info::transform_info(glm::vec3 pos, glm::vec3 rot, glm::vec3 scale) {
        this->position = pos;
        this->rotation = rot;
        this->scale = scale;

        forward = glm::vec3(0, 0, -1);
        right = glm::vec3(1, 0, 0);
        up = glm::vec3(0, 1, 0);

        update_transforms();
    }

    void transform_info::update_transforms() {
        transforms = glm::eulerAngleYXZ(rotation.x, rotation.y, rotation.z);

        up = transforms * glm::vec4(0, 1, 0, 0);
        right = transforms * glm::vec4(1, 0, 0, 0);
        forward = transforms * glm::vec4(0, 0, -1, 0);

        transforms = glm::scale(glm::mat4(1.0f), scale) * transforms;
        transforms = glm::translate(glm::mat4(1.0f), position) * transforms;
    }
}
