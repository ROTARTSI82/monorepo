//
// Created by Grant on 2019-08-26.
//

#include "mason/gl/window.h"

namespace mason::gl {
    Window::Window(int width, int height, const char *title, GLFWmonitor *mon, GLFWwindow *share) {
        if (!glfwHasInit) {
            initGLFW();
        }

        win = glfwCreateWindow(width, height, title, mon, share);
        if (!win) {
                    MASON_CRITICAL("Failed to create window: win = {}", win);
            glfwTerminate();
            std::terminate();
        }

        glfwMakeContextCurrent(win);

        if (!glewHasInit) {
            initGlew();
        }
    }

    Window::~Window() {
        if (win != nullptr) {
            glfwDestroyWindow(win);
        }
//        glfwTerminate();
    }

    void Window::bind() {
        glfwMakeContextCurrent(win);
    }

    bool Window::isClosed() {
        return glfwWindowShouldClose(win);
    }

    void Window::clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    void Window::flip() {
        glfwSwapBuffers(win);
        glfwPollEvents();
    }
}