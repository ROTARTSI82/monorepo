#include "cel/eng/glfw_win.hpp"

namespace cel {
    void glfw_window::initialize() {
        glfwInit();
    }

    void glfw_window::terminate() {
        glfwTerminate();
    }

    glfw_window::glfw_window(int width, int height) {

    }

    glfw_window::~glfw_window() {
        glfwDestroyWindow(win);
    }
}