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

namespace cel {
    typedef float float_t;

    constexpr auto extra_sprintf_space = 128;
    constexpr auto log_strftime_size = 32;
    constexpr auto log_strftime_fmt = "";

    template <typename T>
    class consts_for {
    public:
        T pi, root2;

        static GLenum gl_type_enum;

        static inline const consts_for<T> &calc() {
            static const consts_for<T> var = consts_for<T>();
            return var;
        }

    private:
        consts_for() noexcept {
            std::cout << "new calculated_constants was constructed" << std::endl;
            pi = std::acos((T) (-1));
            root2 = std::sqrt((T) 2);
        }
    };
}

#endif