//
// Created by Grant on 9/21/19.
//

#pragma once

#ifndef __MASON_CAMERA_H
#define __MASON_CAMERA_H

#include "mason/transform_info.h"

#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"

namespace mason {
    class camera {
    public:
        mason::transform_info transforms;
        float fov = 70, near = 0.1, far = 100;
        bool use_ortho = false;
        float width, height;

        explicit camera(float width, float height, glm::vec3 pos = glm::vec3(0), glm::vec3 rot = glm::vec3(0));

        glm::mat4 get_view();

        glm::mat4 get_projection();
    };
}

#endif //__MASON_CAMERA_H
