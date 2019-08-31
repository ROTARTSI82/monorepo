//
// Created by Grant on 2019-08-26.
//

#include "mason/gl/window.h"

namespace mason::gl {
    GLWindow::GLWindow(int width, int height, const char *title, GLFWmonitor *mon, GLFWwindow *share) {

        initGLFW();
        const GLFWvidmode *mode;

        if (mon != nullptr) {
            mode = glfwGetVideoMode(mon);
            monitor = mon;
        } else {
            mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RED_BITS, mode->redBits);
        glfwWindowHint(GLFW_GREEN_BITS, mode->greenBits);
        glfwWindowHint(GLFW_BLUE_BITS, mode->blueBits);
        glfwWindowHint(GLFW_REFRESH_RATE, mode->refreshRate);

        if (!width) {
            width = mode->width;
        }

        if (!height) {
            height = mode->height;
        }

        std::cout << "bf win = " << win << std::endl;
        win = glfwCreateWindow(width, height, title, mon, share);
        std::cout << "af win = " << win << std::endl;
        if (!win) {
            MASON_CRITICAL("Failed to create window! Terminating...");
            glfwTerminate();
            std::terminate();
        }

        bind();

        initGLEW();
    }

    GLWindow::GLWindow(mason::gl::GLWindow *parent) {
        initGLFW();
        const GLFWvidmode *mode;

        monitor = parent->monitor;
        if (monitor != nullptr) {
            mode = glfwGetVideoMode(monitor);
        } else {
            mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RED_BITS, mode->redBits);
        glfwWindowHint(GLFW_GREEN_BITS, mode->greenBits);
        glfwWindowHint(GLFW_BLUE_BITS, mode->blueBits);
        glfwWindowHint(GLFW_REFRESH_RATE, mode->refreshRate);

        int width, height;
        glfwGetWindowSize(parent->win, &width, &height);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        win = glfwCreateWindow(width, height, "ThreadSlave", parent->monitor, parent->win);
        if (!win) {
            MASON_FATAL("NOOO! win slave is null!");
        }

        bind();
        initGLEW();
    }

    GLWindow::~GLWindow() {
        destroy();
    }

    void GLWindow::destroy() {
        if (win != nullptr) {
            glfwDestroyWindow(win);
        }
    }

    void GLWindow::bind() {
        glfwMakeContextCurrent(win);
    }

    bool GLWindow::shouldClose() {
        return glfwWindowShouldClose(win);
    }

    void GLWindow::clear() {
        bind();
        glClear(GL_COLOR_BUFFER_BIT);
    }

    void GLWindow::flip() {
        bind();
        glfwSwapBuffers(win);
        glfwPollEvents();
    }
}