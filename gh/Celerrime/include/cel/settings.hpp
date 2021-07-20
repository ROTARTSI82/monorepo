/**
 * @file settings.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-12
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_SETTINGS_HPP
#define CEL_SETTINGS_HPP

#include "cel/constants.hpp"

namespace cel {

    enum class gui_scale_mode : uint8_t {
        manual,
        automatic
    };

    class settings_handler {
    public:
        bool save_on_destroy = false;

        gui_scale_mode scale_mode = gui_scale_mode::automatic;
        float_t manual_scale = 1;

        bool enforce_aspect = true;
        float_t game_aspect = consts_for<float_t>::calc().root2;

        float_t fovy = 90;
        float_t tas_fps = 60;

        settings_handler();
        ~settings_handler();

        settings_handler(const settings_handler &rhs) noexcept = default;
        settings_handler &operator=(const settings_handler &rhs) noexcept = default;

        void save();
    };
}

#endif