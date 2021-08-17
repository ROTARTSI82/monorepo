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
#include "cel/eng/window.hpp"

#include <unordered_map>
#include <functional>

namespace cel {

    enum class gui_scale_mode : uint8_t {
        manual,
        automatic
    };

    class settings_handler {
    public:
        bool save_on_destroy = false;

        std::locale locale;

        gui_scale_mode scale_mode = gui_scale_mode::automatic;
        float_t manual_scale = 1;

        bool enforce_aspect = true;
        float_t aspect = consts_for<float_t>::root2();

        int win_width = 1440, win_height = 900;
        std::function<generic_vec2<GLsizei>(int viewport_w, int viewport_h)> fb_size_resolver = [](int w, int h) -> generic_vec2<GLsizei> {
            return {w, h};
        };

        float_t fovy = 90;
        float_t tas_fps = 60;

        std::unordered_map<std::string, std::string> strs;

        settings_handler(int argc, char **argv);
        ~settings_handler();

        inline void reload_locale() const { std::locale::global(locale); };

        settings_handler(const settings_handler &rhs) noexcept = default;
        settings_handler &operator=(const settings_handler &rhs) noexcept = default;

        void load();
        void save();

        std::string res_path(const std::string &file, bool essential = false);

        std::string txt(const std::string &key);
    };
}

#endif