//
// Created by Grant on 2019-08-26.
//

#include "mason/gl/window.h"

namespace mason::gl {
    void initGLEW() {  // Only call this after you have a valid window
        glewExperimental = GL_TRUE;
        GLenum glew = glewInit();
        if (glew != GLEW_OK) {
            MASON_CRITICAL("Failed to init GLEW: glewInit() = {}", glew)
            std::terminate();
        }
        MASON_INFO("Successfully init GLEW {}", glewGetString(GLEW_VERSION))
    }

    void initGL() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendEquation(GL_ADD);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);

//        glEnable(GL_STENCIL_TEST);
//        glStencilMask(0x00);  // Everything gets written as-is
//        glStencilFunc(GL_ALWAYS, 0, 0x00);  // Everything passes
//        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);  // Keep everything

        Texture2D::activateSlot(GL_TEXTURE0);

        MASON_INFO("Initialized OpenGL {}", glGetString(GL_VERSION))
        MASON_INFO("OpenGL Vendor: {}", glGetString(GL_VENDOR))
        MASON_INFO("GL Shading Language Version: ", glGetString(GL_SHADING_LANGUAGE_VERSION))
        MASON_INFO("OpenGL Extensions: {}", glGetString(GL_EXTENSIONS))
    }

    void initGLFW() {

        int stat = glfwInit();
        if (!stat) {
            MASON_CRITICAL("Failed to init GLFW: glfwInit() = {}", stat)
            std::terminate();
        }
        MASON_INFO("Successfully init GLFW {}", glfwGetVersionString())
    }

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

        win = glfwCreateWindow(width, height, title, mon, share);
        if (!win) {
            MASON_CRITICAL("Failed to create window! Terminating...");
            glfwTerminate();
            std::terminate();
        }

        bind();
        initGLEW();
        initGL();
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
            MASON_FATAL("Failed to copy window! Ignoring.")
        }

        bind();
        initGLEW();
        initGL();
    }

    GLWindow::~GLWindow() {
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
        glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        double now = glfwGetTime();
        deltaTime = now - lastFrame;
        lastFrame = now;
    }

    void GLWindow::flip() {
        bind();
        glfwSwapBuffers(win);
        glfwPollEvents();
    }

    void GLWindow::setClearColor(GLclampf r, GLclampf g, GLclampf b, GLclampf a) {
        clearColor = {r, g, b, a};
    }

    void GLWindow::draw(GameObject *obj) {
        if (useVAOs) {
            obj->model->vbo->bind();
            obj->model->vao->bind();
        } else {
            obj->model->vbo->reloadLayout();
        }

        obj->model->ibo->bind();
        obj->shader->bind();
        Texture2D::activateSlot(GL_TEXTURE0);
        obj->texture->bind();
        obj->shader->setUniform1i("u_Texture", 0);
        obj->shader->setUniformMat4f("u_ModelMatrix", obj->transform);
        obj->shader->setUniformMat4f("u_ViewMatrix", view);
        obj->shader->setUniformMat4f("u_ProjectionMatrix", projection);

        obj->preRender();

        glDrawElements(obj->model->drawMode, obj->model->ibo->count, obj->model->ibo->dataType, nullptr);
    }

    void GLWindow::drawAll() {
        for (GameObject *go : renderQueue) {
            draw(go);
        }
    }
}