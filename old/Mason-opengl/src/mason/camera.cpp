//
// Created by Grant on 9/21/19.
//

#include "mason/camera.h"

namespace mason {
    camera::camera(float width, float height, glm::vec3 pos, glm::vec3 rot) : width(width), height(height) {
        transforms = transform_info(pos, rot);
    }

    glm::mat4 camera::get_view() {
        return glm::lookAt(transforms.position, transforms.forward + transforms.position, transforms.up);
    }

    glm::mat4 camera::get_projection() {
        if (!use_ortho) {
            return glm::ortho(0.0f, width, height, 0.0f, near, far);
        } else {
            return glm::perspective(fov, width / height, near, far);
        }
    }
}
