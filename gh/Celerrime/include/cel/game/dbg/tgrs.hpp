/**
 * @file tgrs.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-08-16
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_GAME_DBG_TGRS_HPP
#define CEL_GAME_DBG_TGRS_HPP

#include <cel/eng/gl.hpp>
#include "cel/world.hpp"
#include "cel/eng/window.hpp"
#include "cel/eng/phys.hpp"

namespace cel::dbg {
    class tgrs_layer : public layer {
    private:
        world_t &parent;
        shader_pipeline shaders;

        draw_call draw_info;

        texture_ref sq_tex;

        std::vector<vec2> lvl_finishes = {{700, 300},
                                          {700, 300},
                                          {700, 300},
                                          {0, 400},
                                          {650, 300},
                                          {300, 0},
                                          {900, 700}};


        std::vector<std::vector<aligned_rect_collider>> lvl_spikes = {
            {},
            {{{250 + 10, 230 + 10}, {10, 10}}, {{450 + 10, 80 + 10}, {10, 10}}, {{450 + 10, 380 + 10}, {10, 10}}}, 
            {},
            {},
            {{{300 + 130, 500 + 25}, {130, 25}}, {{600 + 75, 400 + 10}, {75, 10}}},
            {},
            {}
        }, lvl_platforms = {
            {{{0 + 400, 550 + 25}, {400, 25}}, {{100 + 350, 450 + 50}, {350, 50}}},
            {{{0 + 250, 100 + 25}, {250, 25}}, {{100 + 350, 250 + 25}, {350, 25}}, {{0 + 400, 400 + 25}, {400, 25}}},
            {{{0 + 150, 550 + 25}, {150, 25}}, {{500 + 150, 550 + 25}, {150, 25}}, {{550 + 150, 400 + 12}, {150, 12}}, {{555 + 12, 300 + 50}, {12, 50}}, {{555 + 100, 275 + 12}, {100, 12}}},
            {{{0 + 50, 350 + 25}, {50, 25}}},
            {{{0 + 400, 550 + 25}, {400, 25}}},
            {{{0 + 45, 550 + 15}, {45, 15}}, {{400 + 15, 500 + 15}, {15, 15}}, {{400 + 15, 350 + 15}, {15, 15}}, {{200 + 15, 200 + 15}, {15, 15}}},
            {{{0 + 400, 550 + 25}, {400, 25}}}
        };

        aligned_rect_collider player{{40, 60}, {10, 10}}, finish{{}, {25, 50}};
        vec2 vel, ground_normal, jmp_norm;
        int jump_frames = 0;
        uint8_t grounded = false, level = 0;


        void load_map(int n);

    public:
        tgrs_layer(world_t &w);
        virtual ~tgrs_layer() = default;

        void tick(const input_frame &input) override;
        void upload() override;
        void draw() override;
        void enqueue_render() override;
    };
}

#endif