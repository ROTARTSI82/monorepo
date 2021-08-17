/**
 * @file phys_test.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-08-16
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_GAME_DBG_PHYS_TEST_HPP
#define CEL_GAME_DBG_PHYS_TEST_HPP

#include <cel/eng/gl.hpp>
#include "cel/world.hpp"
#include "cel/eng/window.hpp"
#include "cel/eng/phys.hpp"

namespace cel::dbg {
    class physics_test_layer : public layer {
    private:
        shader_pipeline shaders;

        gl_mat4 view_mat{identity_tag};
        gl_mat4 proj_mat = mat4::perspective(90.0, 1440.0 / 900, 0.1, 100.0);
        draw_call draw_info;

        texture sq_tex;

        float depth = -10.0;

        aligned_rect_collider a{{5, 1}, {0, 0}};
        aligned_rect_collider b{{10, 1}, {2, 2}};

        vec2 push;
        uint8_t status;

        GLint proj_su, view_su;

    public:
        physics_test_layer(world_t &w);
        virtual ~physics_test_layer() = default;

        void tick(const input_frame &input) override;
        void upload() override;
        void draw() override;
        void enqueue_render() override;
    };
}

#endif