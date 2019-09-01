//
// Created by Grant on 2019-08-31.
//

#include "mason/gl/camera.h"
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

namespace mason::gl {
    Camera::Camera(glm::vec3 pos, glm::vec2 look, GLWindow *window) : lookAngle(look), position(pos), win(window) {
        win = window;
        lookAngle = look;
        position = pos;

        lookDirection = glm::vec3(cos(lookAngle.y) * sin(lookAngle.x), sin(lookAngle.y),
                                  cos(lookAngle.y) * cos(lookAngle.x));

        right = glm::vec3(sin(lookAngle.x - pi / 2.0f), 0, cos(lookAngle.x - pi / 2.0f));
        forward = glm::vec3(sin(lookAngle.x), 0, cos(lookAngle.x));
        MASON_INFO("right = {}, {}, {} | lookDir = {}, {}, {}", right.x, right.y, right.z, lookDirection.x,
                   lookDirection.y, lookDirection.z);
//        up = glm::cross(right, lookDirection);
        up = glm::cross(right, lookDirection);
    }

    void Camera::move(float forwardAmount, float rightAmount) {
        position += forward * forwardAmount * (float) win->deltaTime;
        position += right * rightAmount * (float) win->deltaTime;
    }

    void Camera::look(glm::vec2 amount) {
        lookAngle += amount * (float) win->deltaTime;

        if (lookAngle.y > mason::pi / 2) {
            lookAngle.y = mason::pi / 2;
        } else if (lookAngle.y < -mason::pi / 2) {
            lookAngle.y = -mason::pi / 2;
        }

        lookDirection = glm::vec3(cos(lookAngle.y) * sin(lookAngle.x), sin(lookAngle.y),
                                  cos(lookAngle.y) * cos(lookAngle.x));

        right = glm::vec3(sin(lookAngle.x - pi / 2.0f), 0, cos(lookAngle.x - pi / 2.0f));
        forward = glm::vec3(sin(lookAngle.x), 0, cos(lookAngle.x));
        up = glm::cross(right, lookDirection);
    }

    void Camera::move(float forwardAmount, float rightAmount, float dt) {
        position += forward * forwardAmount * dt;
        position += right * rightAmount * dt;
    }

    void Camera::look(glm::vec2 amount, float dt) {
        lookAngle += amount * dt;

        if (lookAngle.y > mason::pi / 2) {
            lookAngle.y = mason::pi / 2;
        } else if (lookAngle.y < -mason::pi / 2) {
            lookAngle.y = -mason::pi / 2;
        }

        lookDirection = glm::vec3(cos(lookAngle.y) * sin(lookAngle.x), sin(lookAngle.y),
                                  cos(lookAngle.y) * cos(lookAngle.x));

        right = glm::vec3(sin(lookAngle.x - pi / 2.0f), 0, cos(lookAngle.x - pi / 2.0f));
        forward = glm::vec3(sin(lookAngle.x), 0, cos(lookAngle.x));
        up = glm::cross(right, lookDirection);
    }

    glm::mat4 Camera::getProjection() {
        int width, height;
        glfwGetWindowSize(win->win, &width, &height);

        if (width && height) {
            return glm::perspective(glm::radians(fov), (float) width / (float) height, near, far);
        } else {
            return glm::mat4(1.0f);
        }
    }

    glm::mat4 Camera::getView() {
        return glm::lookAt(position, position + lookDirection, up);
    }
}

