#include <iostream>
#include "mason/app_node.h"
#include "mason/gl/gl_window.h"

mason::gl::gl_window *win;

class custom_layer : public mason::layer {
public:
    int x;
    bool parState;

    custom_layer(int id, bool ps) : x(id), parState(ps) {};

    void on_attach() {
        std::cout << "Layer " << x << " was attached!" << std::endl;
    }

    void on_detach() {
        std::cout << "Layer " << x << " was detached!" << std::endl;
    }

    void on_update(int thread);
};

class custom_app : public mason::app_node {
public:
    bool s;

    custom_app(bool x) : s(x) {}

    void on_activate() {
        std::cout << "app was activated!" << std::endl;
    };

    void on_deactivate() {
        std::cout << "app was deactivated!" << std::endl;
    };
};

void custom_layer::on_update(int thread) {
    win->clear();
    win->flip();
}

int main() {
    mason::init_logging(true);
    MASON_FATAL("Hello World! This is an SPDLOG test!");
    win = new mason::gl::gl_window();
    custom_app *app = new custom_app(false);
    app->push_overlay(new custom_layer(1, false));
    app->on_activate();
    while (!win->poll_close()) {
        app->update(1);
    }
    app->on_deactivate();
    delete app;

}
