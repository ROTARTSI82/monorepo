/**
 * @file constants.hpp
 * @brief 
 * @date 2021-07-11
 */

#pragma once

#ifndef CEL_CONSTANTS_HPP
#define CEL_CONSTANTS_HPP

#include <GL/glew.h>
#include <cmath>
#include <iostream>

#define CEL_ENABLE_LOG 1
#define CEL_USE_OGL_DBG_CTX GLFW_TRUE


#define UINT8_T static_cast<uint8_t>

#define CEL_VERSION __DATE__ " " __TIME__

namespace cel {
    constexpr float znear = 0.1f;
    constexpr float zfar = 100.0f;

    constexpr float grounded_threshold = 0.1f;

    typedef float float_t;

    template <typename T>
    class consts_for {
    public:
        static constexpr T pi() {
            return std::acos((T) -1);
        }

        static constexpr T root2() {
            return std::sqrt((T) 2);
        }

        static constexpr GLenum gl_type_enum = 0; // definitions in constants.cpp
    };

    template<>
    constexpr GLenum consts_for<float>::gl_type_enum = GL_FLOAT;
}

#endif