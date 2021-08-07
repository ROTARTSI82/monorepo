/**
 * @file window.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-19
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_WINDOW_HPP
#define CEL_ENG_WINDOW_HPP

#include <cstdint>
#include <vector>
#include <mutex>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "cel/eng/gl.hpp"
#include "cel/eng/linalg.hpp"
#include "cel/eng/input.hpp"

namespace cel {

    struct draw_instance {
        gl_mat2 transform;
        gl_vec3 center;
        gl_float alpha_mult;

        gl_vec2 sample_origin;
        gl_vec2 sample_extent;
        gl_vec2 sample_ntiles;
    };

    class draw_call {
    private:
        GLuint vbo;
        vertex_array vao{};

    public:
        draw_instance *instances; // NOTE: Using a static array instead of std::vector because the VBO must be static anyways.
        unsigned num_blits = 0;
        bool do_flush = true;
        std::mutex mtx;

        draw_call(size_t max_instances);
        ~draw_call();

        inline void flush() {
            do_flush = false;
            glBindBuffer(GL_COPY_WRITE_BUFFER, vbo);
            glBufferSubData(GL_COPY_WRITE_BUFFER, 0, num_blits * sizeof(draw_instance), instances);
        }

        inline void flush_locked() {
            std::scoped_lock lock(mtx);
            flush();
        }

        inline void dispatch() {
            vao.bind();
            glDrawArraysInstanced(GL_TRIANGLES, 0, 6, num_blits);
        }
    };

    enum class window_types : uint8_t {
        control,
        game,
        size
    };

    class settings_handler;

    class window {
    private:
        GLFWwindow *win;

        settings_handler *settings;
        window_types type;

        input_provider *inp;

    public:

        std::vector<draw_call> world_draws; // drawn with perspective.
        std::vector<draw_call> gui_draws; // drawn with the orthographic projection.

        window(settings_handler *settings, window_types type);
        ~window();

        window &operator=(const window &rhs) noexcept = delete;
        window(const window &rhs) noexcept = delete;

        void next();
        inline input_frame input_for(uint64_t frame_number) { return inp->next(frame_number, this); }

        inline bool running() { return !glfwWindowShouldClose(win); }
    };

}

#endif