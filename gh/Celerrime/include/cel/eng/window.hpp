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

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "cel/eng/gl.hpp"
#include "cel/eng/linalg.hpp"
#include "cel/eng/input.hpp"

namespace cel {

    struct draw_instance {
        vec2 sample_origin;
        vec2 sample_extent;
        vec2 sample_ntiles;

        mat2 transform;
        vec2 center;
    };

    class draw_call {
    private:
        GLuint vbo;
        vertex_array vao;

    public:
        std::vector<draw_instance> instances;
        bool do_flush = true;

        void dispatch();
    };
    
    class gui_widget {
    public:
        gui_widget *anchor = nullptr; // nullptr for window.
        vec2 coords{}; // relative to anchor.
        bool rebuild_required = false;

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