//
// Created by Grant on 2019-08-24.
//
#pragma once

#ifndef ROTARTSI_MASON_EVENT_H
#define ROTARTSI_MASON_EVENT_H

#include "mason/gl/gl_includes.h"

namespace mason {

    class EventHandler {
    public:
        static void key(GLFWwindow *win, int, int, int, int); // done

        static void mousemotion(GLFWwindow *win, double x, double y); // done

        static void mousefocus(GLFWwindow *win, int focus); // done

        static void mousebutton(GLFWwindow *win, int button, int action, int mods); // done

        static void mousescroll(GLFWwindow *win, double x, double y); // done

        static void joystick(int joyID, int joyEv); // done

        static void filedrop(GLFWwindow *win, int num, const char **files); // done

        static void windowresize(GLFWwindow *win, int w, int h); // done

        static void windowclose(GLFWwindow *win); // done

        static void windowfocus(GLFWwindow *win, int focused);

        static void windowicon(GLFWwindow *win, int iconed); // done
    };


    enum EventType {
        KEY, UNDEFINED, MOUSEMOTION, MOUSEFOCUS, MOUSEBUTTON, MOUSESCROLL, JOYSTICKEVENT, FILEDROP,
        WINDOWRESIZE, WINDOWCLOSE, WINDOWFOCUS, WINDOWICONIFY
    };
    struct Event {
    public:
        EventType type = UNDEFINED;
        GLFWwindow *window = nullptr;
        bool handled = false;
    };

    struct WindowResizeEvent : Event {
    public:
        int width = 0;
        int height = 0;
    };

    struct WindowCloseEvent : Event {
    };

    struct WindowFocusEvent : Event {
    public:
        int focused = 0;
    };

    struct WindowIconifyEvent : Event {
    public:
        int iconified = 0;
    };

    struct KeyEvent : Event {
    public:
        int key = 0;
        int scancode = 0;
        int action = 0;
        int mods = 0;
    };

    struct MouseMotionEvent : Event {
    public:
        double x = 0;
        double y = 0;
    };

    struct MouseFocusEvent : Event {
    public:
        int focused = 0;
    };

    struct MouseButtonEvent : Event {
    public:
        int button = 0;
        int action = 0;
        int mods = 0;
    };

    struct MouseScrollEvent : Event {
    public:
        double x = 0;
        double y = 0;
    };

    struct JoystickEvent : Event {
    public:
        int joyID = 0;
        int joyEvent = 0;
    };

    struct FileDropEvent : Event {
    public:
        int num = 0;
        const char **files = nullptr;
    };

    extern std::queue<Event *> eventQueue;
    extern std::mutex eventMtx;
}

#endif //MASON_EVENT_H
