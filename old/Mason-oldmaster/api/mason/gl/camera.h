//
// Created by Grant on 2019-08-31.
//

#pragma once

#ifndef MASON_CAMERA_H
#define MASON_CAMERA_H

#include "gl_includes.h"

namespace mason {
    double pi = atan(1) * 4;
}

namespace mason::gl {
    class Camera {
    public:
        glm::vec3 up;
        glm::vec3 right;
        glm::vec3 forward;

        glm::vec3 lookDirection;
        glm::vec3 position;

        float fov = 70, near = 0.1f, far = 100.0f;

        glm::vec2 lookAngle;
        GLWindow *win;

        Camera(glm::vec3 pos, glm::vec2 look, GLWindow *window);

        glm::mat4 getProjection();

        glm::mat4 getView();

        void look(glm::vec2 amount);

        void move(float forward, float right);

        void look(glm::vec2 amount, float dt);

        void move(float f, float r, float dt);
    };
}
#endif //MASON_CAMERA_H
