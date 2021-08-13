#include "cel/world.hpp"

#include "cel/app.hpp"

namespace cel {
    world::world(::cel::app *a) : app(a) {}

    world::~world() {

    }

    void world::tick(const input_frame &inp) {
        for (const auto &layer : layers) {
            layer->tick(inp);
        }
    }

    void world::draw() {
        // Is it better to interleave or seperate upload() and draw() ?
        // Does it even matter? when are the calls actually dispatched? glfwSwapBuffers()?
        for (const auto &layer : layers)
            layer->upload(); // glBufferSubData()

        for (const auto &layer : layers)
            layer->draw(); // glDrawArraysInstanced()
    }
}