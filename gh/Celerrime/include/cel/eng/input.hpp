/**
 * @file input.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-19
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_INPUT_HPP
#define CEL_ENG_INPUT_HPP

#include "cel/eng/linalg.hpp"

namespace cel {
    // indices into input_frame::buttons (TODO: Use packed bit array?)
    enum class button_id : uint8_t {
        w, a, s, d,
        open_inventory,
        size
    };

    struct input_frame {
        vec2 direction;
        bool buttons[static_cast<unsigned long>(button_id::size)];
    };

    class window;
    
    class input_provider {
    public:
        virtual input_frame next(uint64_t frame_num, window *win) = 0;
    };


}
#endif