//
// Created by Grant on 2019-08-26.
//

#pragma once

#ifndef MASON_OPENGL_WINDOW_H
#define MASON_OPENGL_WINDOW_H

#include "mason/masonpch.h"
#include "mason/log.h"
#include "mason/mason.h"

#include "GL/glew.h"
#include "GLFW/glfw3.h"

namespace mason::gl {
    class GLWindow : public mason::Window {
    public:
        GLFWwindow *win = nullptr;
        GLFWmonitor *monitor = nullptr;

        explicit GLWindow(int w = 0, int h = 0, const char *t = "Mason Engine", GLFWmonitor *m = nullptr,
               GLFWwindow *s = nullptr);

        explicit GLWindow(GLWindow *parent);

        ~GLWindow() override;

        void bind() override;

        bool shouldClose() override;

        void clear() override;

        void submitRender() override {};

        void asyncSubmitRender() override {};

        void destroy() override;

        void flip() override;
    };

    std::vector<std::thread::id> glewInitThreads;
    std::vector<std::thread::id> glfwInitThreads;

    void initGLEW() {  // Only call this after you have a valid window
        if (std::find(glewInitThreads.begin(), glewInitThreads.end(), std::this_thread::get_id()) !=
            glewInitThreads.end()) {
            return;
        }

        glewExperimental = GL_TRUE;
        GLenum glew = glewInit();
        if (glew != GLEW_OK) {
            MASON_CRITICAL("Failed to init GLEW: glewInit() = {}", glew)
            std::terminate();
        }
        MASON_INFO("Successfully init GLEW {}", glewGetString(GLEW_VERSION))
        std::cout << "init glew for " << std::this_thread::get_id() << std::endl;
        glewInitThreads.push_back(std::this_thread::get_id());
    }

    void initGLFW() {
        // If this thread's id is in glfwInitThreads, return.
        if (std::find(glfwInitThreads.begin(), glfwInitThreads.end(), std::this_thread::get_id()) !=
            glfwInitThreads.end()) {
            return;
        }

        int stat = glfwInit();
        if (!stat) {
            MASON_CRITICAL("Failed to init GLFW: glfwInit() = {}", stat)
            std::terminate();
        }
        MASON_INFO("Successfully init GLFW {}", glfwGetVersionString())
        std::cout << "init glfw for " << std::this_thread::get_id() << std::endl;
        glfwInitThreads.push_back(std::this_thread::get_id());
    }
}

#endif //MASON_WINDOW_H
