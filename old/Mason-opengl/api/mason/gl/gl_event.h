//
// Created by 25granty on 10/4/19.
//

#pragma once

#ifndef MASONSHARE_GL_EVENT_H
#define MASONSHARE_GL_EVENT_H

#include "mason/gl/gl_core.h"
#include "mason/gl/gl_window.h"

namespace mason::gl {
    enum gl_event_type {
        key, mouse_motion, mouse_button, invalid
    };

    struct gl_event {
        GLFWwindow *win = nullptr;
        gl_event_type type = invalid;
    };

    struct gl_key_event : gl_event {
        int key = 0;
        int scancode = 0;
        int action = 0;
        int mods = 0;
    };

    struct gl_mouse_motion_event : gl_event {
        double x = 0;
        double y = 0;
    };

    struct gl_mouse_button_event : gl_event {
        int button = 0;
        int action = 0;
        int mods = 0;
    };
}


#endif //MASONSHARE_GL_EVENT_H
