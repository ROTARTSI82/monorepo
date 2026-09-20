/**
 * @file misc_defs.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-09-26
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CSC_MISC_DEFS_HPP
#define CSC_MISC_DEFS_HPP

#include <cstdint>

#define COMPRESSION_THRESHOLD 512

namespace csc {
    enum class Gamemode : uint8_t {
        survival = 0,
        creative = 1,
        adventure = 2,
        spectator = 3,
    };

    enum class Difficulty : uint8_t {
        peaceful = 0,
        easy = 1,
        normal = 2,
        hard = 3,
    };
}

#endif