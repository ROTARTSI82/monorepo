/**
 * @file strings.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-23
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_STRINGS_HPP
#define CEL_ENG_STRINGS_HPP

#include <string>

#include "cel/constants.hpp"

namespace cel {
    class settings_handler;

    class string_dictionary {
    public:
        string_dictionary() = default;
        string_dictionary(settings_handler *settings);

        std::string win_title = "Celerrime " CEL_VERSION;
    };
}

#endif