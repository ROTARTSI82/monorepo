//
// Created by Grant on 2019-08-26.
//

#pragma once

#ifndef MASON_OPENGL_WINDOW_H
#define MASON_OPENGL_WINDOW_H

#include "mason/masonpch.h"
#include "mason/log.h"
#include "mason/mason.h"
#include "mason/gl/texture.h"
#include "mason/gl/gl_objects.h"
#include "mason/gl/shaders.h"

#include "GL/glew.h"
#include "GLFW/glfw3.h"
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

namespace mason::gl {

    struct Model {
        VertexArray *vao;
        VertexBuffer *vbo;
        IndexBuffer *ibo;
        GLenum drawMode;
    };

    struct GameObject {
        Model *model;
        ShaderProgram *shader;
        Texture2D *texture;
        glm::mat4 transform;

        void (*preRender)();
    };

    class GLWindow {
    public:
        GLFWwindow *win = nullptr;
        GLFWmonitor *monitor = nullptr;

        glm::mat4 view = glm::mat4(1.0);
        glm::mat4 projection = glm::mat4(1.0);

        std::vector<GameObject *> renderQueue;

        double lastFrame = 0;
        double deltaTime = 0;

        bool useVAOs = false;

        glm::vec4 clearColor = glm::vec4(1.0);

        explicit GLWindow(int w = 0, int h = 0, const char *t = "Mason Engine", GLFWmonitor *m = nullptr,
               GLFWwindow *s = nullptr);

        explicit GLWindow(GLWindow *parent);

        ~GLWindow();

        void bind();

        void draw(GameObject *obj);

        void drawAll();

        bool shouldClose();

        void clear();

        void setClearColor(GLclampf r, GLclampf g, GLclampf b, GLclampf a);

        void flip();
    };

    void initGLEW();

    void initGL();

    void initGLFW();
}

#endif //MASON_WINDOW_H
