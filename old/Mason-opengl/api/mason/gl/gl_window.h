//
// Created by Grant on 9/26/19.
//

#pragma once

#ifndef MASONSHARE_GL_WINDOW_H
#define MASONSHARE_GL_WINDOW_H

#include "mason/app_node.h"
#include "mason/gl/gl_core.h"
#include "mason/gl/gl_event_handler.h"

#include "glm/glm.hpp"

namespace mason::gl {
    class gl_window {
    public:
        GLFWwindow *win;
        glm::vec4 clear_color = glm::vec4(0);

        explicit gl_window(const int &w = 640, const int &h = 480, const char *title = "Untitled Window");

        virtual ~gl_window();

        void init_imgui();

        void bind();

        void clear();

        void flip();

        int poll_close();
    };
}

#endif //MASONSHARE_GL_WINDOW_H
