/**
 * @file seesaw_rocket.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-10-10
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_GAME_ETC_SS_ROCKET_HPP
#define CEL_GAME_ETC_SS_ROCKET_HPP

#include <cel/eng/gl.hpp>
#include "cel/world.hpp"
#include "cel/eng/window.hpp"
#include "cel/eng/audio.hpp"

namespace cel::etc {

    class ssrocket_layer : public layer {
    private:
        float gravity = 0.003;

        texture_ref rocket_tex;
        draw_call call;
        world_t *wrld;

        mat4 projection = mat4::ortho(-75, 75, -75, 75, 0.01, 100);

        struct rocket_t {
            vec2 pos{};
            vec2 vel{};
            float theta = consts_for<float>::pi() / 2;
            float dtheta = 0;

            float mass = 1100; // kg

            float fuel = 100; // kg
            float max_thrust = 0.003 * mass * 2; // newtons per tenth of a second
            float specific_impulse = 0.1; // kg per (newton * 60th of a second)

            float thruster_radius = 0.01; // distance the thrusters are from the center
            float thruster_mass = 1100 * 4;
        };
        
        rocket_t rocket{};

    public:
        ssrocket_layer(world_t &w);
        virtual ~ssrocket_layer() = default;

        void tick(const input_frame &input) override;
        void upload() override;
        void draw() override;
        void enqueue_render() override;
    };
}

#endif