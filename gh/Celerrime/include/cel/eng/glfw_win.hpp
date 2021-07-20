/**
 * @file glfw_win.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-19
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_GLFW_WIN_HPP
#define CEL_ENG_GLFW_WIN_HPP

#include "cel/eng/window.hpp"

#include <GLFW/glfw3.h>

namespace cel {
    class glfw_window : public window {
    private:
        GLFWwindow *win;

    public:

        static void initialize();
        static void terminate();

        glfw_window(int width, int height);
        ~glfw_window() override;

        glfw_window &operator=(const glfw_window &rhs) noexcept = delete;
        glfw_window(const glfw_window &rhs) noexcept = delete;
    };
}

#endif