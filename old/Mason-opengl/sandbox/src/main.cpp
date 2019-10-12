#include <iostream>
#include "mason/app_node.h"
#include "mason/gl/gl_window.h"
#include "mason/gl/shader_program.h"
#include "mason/gl/vertex_buffer.h"
#include "mason/gl/index_buffer.h"
#include "mason/gl/vb_layout.h"
#include "mason/gl/texture2d.h"

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

    bool on_event(mason::gl::gl_event *ev) {
        MASON_INFO("Got event!");
        return false;
    }
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
}

int main() {
    mason::init_logging(true);
    MASON_FATAL("Hello World! This is an SPDLOG test!");
    win = new mason::gl::gl_window();
    custom_app *app = new custom_app(false);
    mason::gl::event_handler::add_event_subscriber(app);

    mason::image *img = new mason::image("/Users/25granty/Desktop/test.png");

    mason::gl::texture2d *tex = new mason::gl::texture2d(img);
    tex->bind_slot();

    mason::gl::shader_program *prog = new mason::gl::shader_program(
            "/Users/25granty/Desktop/CLion/Mason/sandbox/res/shader_program");
    prog->bind();
    prog->set_uniform_1i("tex", 0);

    mason::gl::vertex_buffer *vbo =
            new mason::gl::vertex_buffer(
                    mason::gl::pack_vertex_data({
                                                        {{0,  1,  0}, {0,   0,   1.0}, {0,  1},  {0,   0,   1.0, 1.0}},
                                                        {{-1, -1, 0}, {0,   1.0, 0},   {-1, -1}, {0,   1.0, 0,   1.0}},
                                                        {{1,  -1, 0}, {1.0, 0,   0},   {1,  -1}, {1.0, 0,   0,   1.0}}
                                                }));

    mason::gl::index_buffer *ibo = new mason::gl::index_buffer({0, 1, 2});
    mason::gl::get_default_layout()->set_attributes();

    app->push_overlay(new custom_layer(1, false));
    app->on_activate();
    while (!win->poll_close()) {
        mason::gl::event_handler::handle_single_event();
        app->update(1);
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, nullptr);
        win->flip();
    }
    app->on_deactivate();
    delete app;

}
