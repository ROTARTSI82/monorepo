//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef MASONSHARE_VERTEX_DATA_H
#define MASONSHARE_VERTEX_DATA_H

#include "mason/gl/gl_core.h"

#include "glm/glm.hpp"

namespace mason::gl {
    struct vertex_data {
        glm::vec3 position;
        glm::vec3 normal;
        glm::vec2 uv;
        glm::vec4 color = glm::vec4(0);
    };
}

#endif //MASONSHARE_VERTEX_DATA_H
