//
// Created by Grant on 2019-08-24.
//

#include "mason/event.h"

namespace mason {
    std::queue<Event *> eventQueue;
    std::mutex eventMtx;

    void EventHandler::key(GLFWwindow *window, int key, int scancode, int action, int mods) {
        auto n_event = new KeyEvent;
        n_event->window = window;
        n_event->key = key;
        n_event->scancode = scancode;
        n_event->mods = mods;
        n_event->action = action;
        n_event->type = KEY;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    };

    void EventHandler::mousemotion(GLFWwindow *win, double x, double y) {
        auto n_event = new MouseMotionEvent;
        n_event->window = win;
        n_event->x = x;
        n_event->y = y;
        n_event->type = MOUSEMOTION;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::mousefocus(GLFWwindow *win, int focus) {
        auto n_event = new MouseFocusEvent;
        n_event->window = win;
        n_event->focused = focus;
        n_event->type = MOUSEFOCUS;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::mousebutton(GLFWwindow *win, int button, int action, int mods) {
        auto n_event = new MouseButtonEvent;
        n_event->window = win;
        n_event->button = button;
        n_event->action = action;
        n_event->mods = mods;
        n_event->type = MOUSEBUTTON;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::mousescroll(GLFWwindow *win, double x, double y) {
        auto n_event = new MouseScrollEvent;
        n_event->window = win;
        n_event->x = x;
        n_event->y = y;
        n_event->type = MOUSESCROLL;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::joystick(int joyID, int joyEv) {
        auto n_event = new JoystickEvent;
        n_event->window = nullptr;
        n_event->joyID = joyID;
        n_event->joyEvent = joyEv;
        n_event->type = JOYSTICKEVENT;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::filedrop(GLFWwindow *win, int num, const char **files) {
        auto n_event = new FileDropEvent;
        n_event->window = win;
        n_event->num = num;
        n_event->files = files;
        n_event->type = MOUSESCROLL;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::windowresize(GLFWwindow *win, int w, int h) {
        auto n_event = new WindowResizeEvent;
        n_event->window = win;
        n_event->width = w;
        n_event->height = h;
        n_event->type = WINDOWRESIZE;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::windowclose(GLFWwindow *win) {
        auto n_event = new WindowCloseEvent;
        n_event->window = win;
        n_event->type = WINDOWCLOSE;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::windowfocus(GLFWwindow *win, int focused) {
        auto n_event = new WindowFocusEvent;
        n_event->window = win;
        n_event->focused = focused;
        n_event->type = WINDOWFOCUS;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }

    void EventHandler::windowicon(GLFWwindow *win, int iconed) {
        auto n_event = new WindowIconifyEvent;
        n_event->window = win;
        n_event->iconified = iconed;
        n_event->type = WINDOWICONIFY;

        eventMtx.lock();
        eventQueue.push(n_event);
        eventMtx.unlock();
    }
}

