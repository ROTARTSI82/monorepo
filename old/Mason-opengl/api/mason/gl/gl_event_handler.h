//
// Created by 25granty on 10/4/19.
//

#pragma once

#ifndef MASONSHARE_GL_EVENT_HANDLER_H
#define MASONSHARE_GL_EVENT_HANDLER_H

#include "mason/app_node.h"
#include <queue>
#include "mason/gl/gl_core.h"

namespace mason {
    class app_node;

    namespace gl {
        struct gl_event;
    }
}

namespace mason::gl::event_handler {

    extern std::queue<mason::gl::gl_event *> event_queue;
    extern std::mutex event_mutex;
    extern std::vector<mason::app_node *> subscribers;

    void mouse_button_handler(GLFWwindow *win, int button, int action, int mods);

    void mouse_motion_handler(GLFWwindow *win, double x, double y);

    void key_handler(GLFWwindow *win, int key, int scancode, int action, int mods);

    void add_event_subscriber(mason::app_node *app);

    bool handle_single_event();

    void flush_events();
}


#endif //MASONSHARE_GL_EVENT_HANDLER_H
