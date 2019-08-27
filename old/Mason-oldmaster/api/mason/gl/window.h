//
// Created by Grant on 2019-08-26.
//

#pragma once

#ifndef MASON_WINDOW_H
#define MASON_WINDOW_H

#include "mason/masonpch.h"
#include "mason/log.h"

namespace mason::gl {
    class Window {
    public:
        GLFWwindow *win = nullptr;

        Window(int w = 640, int h = 480, const char *t = "Mason Engine", GLFWmonitor *m = nullptr,
               GLFWwindow *s = nullptr);

        ~Window();

        void bind();

        void clear();

        void flip();

        bool isClosed();
    };

    bool glewHasInit = false;
    bool glfwHasInit = false;

    void initGlew() {  // Only call this after you have a valid window
        if (glewHasInit) {
                    MASON_WARN("Tried to call mason::gl::initGlew() when glew has already been init! Ignoring call...");
            return;
        }

        glewExperimental = GL_TRUE;
        GLenum glew = glewInit();
        if (glew != GLEW_OK) {
                    MASON_CRITICAL("Failed to init GLEW: glewInit() = {}", glew);
            std::terminate();
        }
                MASON_INFO("Successfully init GLEW {}", glewGetString(GLEW_VERSION));
        glewHasInit = true;
    }

    void initGLFW() {
        if (glfwHasInit) {
                    MASON_WARN("Tried to call mason::gl::initGLFW() when GLFW has already been init! Ignoring call...");
            return;
        }

        int stat = glfwInit();
        if (!stat) {
                    MASON_CRITICAL("Failed to init GLFW: glfwInit() = {}", stat);
            std::terminate();
        }
                MASON_INFO("Successfully init GLFW {}", glfwGetVersionString());
        glfwHasInit = true;
    }
}

#endif //MASON_WINDOW_H
