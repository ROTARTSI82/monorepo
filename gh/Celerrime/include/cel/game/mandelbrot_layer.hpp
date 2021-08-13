/**
 * @file mandelbrot_layer.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-08-12
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_GAME_MANDELBROT_LAYER_HPP
#define CEL_GAME_MANDELBROT_LAYER_HPP

#include <cel/eng/gl.hpp>
#include "cel/world.hpp"
#include "cel/eng/window.hpp"

namespace cel::game {
    class mandelbrot_layer : public layer {
    private:
        shader_pipeline shaders;

        gl_mat4 view_mat{identity_tag};
        gl_mat4 proj_mat = mat4::perspective(90.0, 1440.0 / 900, 0.1, 100.0);
        draw_call test;

        GLint proj_su, view_su;

    public:
        mandelbrot_layer(world_t &w);
        virtual ~mandelbrot_layer() = default;

        void tick(const input_frame &input) override;
        void upload() override;
        void draw() override;
    };
}

#endif