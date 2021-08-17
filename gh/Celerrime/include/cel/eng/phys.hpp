/**
 * @file phys.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-08-16
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_PHYS_HPP
#define CEL_ENG_PHYS_HPP

#include "cel/eng/linalg.hpp"
#include <tuple>

namespace cel {

    class aligned_rect_collider {
    public:
        // axes contains the semi-extents of the x and y axes of the rect
        vec2 axes, center;

        aligned_rect_collider(vec2 axes, vec2 cent);

        bool collides_with(aligned_rect_collider &other);

        // vec2 is how much to move *this by to push it out of other if it is a TRUE collision
        // uint8_t is 0 for no collision, 1 for "grounded" collision, 3 for TRUE collision
        std::tuple<vec2, uint8_t> collide_direction(aligned_rect_collider &other);
    };
}

#endif