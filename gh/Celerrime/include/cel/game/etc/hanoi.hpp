/**
 * @file hanoi.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-09-10
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_GAME_ETC_HANOI_HPP
#define CEL_GAME_ETC_HANOI_HPP

#include <cel/eng/gl.hpp>
#include "cel/world.hpp"
#include "cel/eng/window.hpp"

namespace cel::etc {

    enum class anim_stage : uint8_t {
        lift_up,
        slide_across,
        drop_down
    };

    struct move_record {
        uint8_t f, t;

        move_record(uint8_t f, uint8_t t) : f(f), t(t) {}
    };

    class hanoi_layer : public layer {
    private:
        texture_ref tex;
        draw_call call;
        world_t *wrld;

        std::vector<move_record> solution;
        int solution_iterator = -1;

        std::vector<uint8_t> src;
        std::vector<uint8_t> aux;
        std::vector<uint8_t> dst;

        uint8_t animated_block = 99; // index of block animated
        uint8_t anim_from; // index of pegs from/to. 0 = src, 1 = aux, 2 = dst
        uint8_t anim_to;
        float from_y = 0, to_y = 0;
        anim_stage stage = anim_stage::lift_up;
        float anim_progress, speed; // between 0 and 1
        // float anim_speed = 0.01; // how much anim_progress is incremented each frame

        int n = 3;

        void enqueue_disks(const std::vector<uint8_t> &vec, float x);
        void load_disks(int n);
        void move(uint8_t f, uint8_t t);

        void solve_recursively(uint8_t n, uint8_t f, uint8_t t);

        // other(0, 1) = 2, other(1, 2) = 0, other(2, 0) = 1. Order of params doesn't matter
        inline uint8_t other(uint8_t f, uint8_t t) {
            // static constexpr uint8_t m[3] = {2, 1, 0};
            return 3 - (f + t);
        }

    public:
        hanoi_layer(world_t &w);
        virtual ~hanoi_layer() = default;

        void tick(const input_frame &input) override;
        void upload() override;
        void draw() override;
        void enqueue_render() override;
    };
}

#endif