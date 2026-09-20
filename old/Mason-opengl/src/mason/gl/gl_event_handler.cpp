//
// Created by 25granty on 10/4/19.
//

#include "mason/gl/gl_event_handler.h"

namespace mason::gl::event_handler {
    std::queue<mason::gl::gl_event *> event_queue;
    std::mutex event_mutex;
    std::vector<mason::app_node *> subscribers;

    void add_event_subscriber(mason::app_node *app) {
        subscribers.emplace_back(app);
    }

    void mouse_button_handler(GLFWwindow *window, int button, int action, int mods) {
        auto ev = new gl_mouse_button_event;
        ev->win = window;
        ev->type = mouse_button;
        ev->action = action;
        ev->button = button;
        ev->mods = mods;

        event_mutex.lock();
        event_queue.push(ev);
        event_mutex.unlock();
    }

    void mouse_motion_handler(GLFWwindow *window, double x, double y) {
        auto ev = new gl_mouse_motion_event;
        ev->win = window;
        ev->type = mouse_motion;
        ev->x = x;
        ev->y = y;

        event_mutex.lock();
        event_queue.push(ev);
        event_mutex.unlock();
    }

    void key_handler(GLFWwindow *window, int key, int scancode, int action, int mods) {
        auto ev = new gl_key_event;
        ev->win = window;
        ev->type = mason::gl::key;
        ev->action = action;
        ev->key = key;
        ev->scancode = scancode;
        ev->mods = mods;

        event_mutex.lock();
        event_queue.push(ev);
        event_mutex.unlock();

    }

    // Returns true if the event que is empty
    bool handle_single_event() {
        event_mutex.lock();
        if (!event_queue.empty()) {
            gl_event *front = event_queue.front();
            event_queue.pop();
            event_mutex.unlock();

            for (app_node *sub : subscribers) {
                if (sub->handle_event(front)) {
                    break;
                }
            }

            delete front;
        } else {
            event_mutex.unlock();
            return true;
        }
        return false;
    }

    void flush_events() {
        while (!handle_single_event());
    }
}
