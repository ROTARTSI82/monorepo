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

struct GLFWwindow;
namespace cel {
    // indices into input_frame::buttons (TODO: Use packed bit array?)
    enum class button_id : uint8_t {
        w, a, s, d,
        open_inventory,

        zoom_in,
        zoom_out,
        size
    };

    struct input_frame {
        gl_vec2 direction = {0, 0};
        bool buttons[static_cast<unsigned long>(button_id::size)];
    };

    std::string to_string(const input_frame &inp);

    class input_provider {
    public:
        virtual ~input_provider() = default;
        virtual input_frame next(uint64_t frame_num, GLFWwindow *win) = 0;
    };

    class hacky_input_provider : public input_provider {
    public:
        hacky_input_provider();
        ~hacky_input_provider() override;
        input_frame next(uint64_t frame_num, GLFWwindow *win) override;
    };


}
#endif