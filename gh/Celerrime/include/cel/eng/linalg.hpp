/**
 * @file linalg.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-11
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_LINALG_HPP
#define CEL_ENG_LINALG_HPP

#include "cel/constants.hpp"

#include <cmath>

namespace cel {
    class identity_tag {};

    template <typename T>
    inline T radians(T deg) {
        return deg * consts_for<T>::calc().pi / 180;
    }

    template <typename T>
    inline T degrees(T rad) {
        return rad * 180 / consts_for<T>::calc().pi;
    }

    template<typename T>
    class generic_vec2;

    template <typename T>
    class generic_mat2 {
    private:
        typedef generic_vec2<T> vec_t;

    public:
        vec_t c0{}, c1{};

        generic_mat2() = default;
        explicit generic_mat2(const identity_tag tag) : c0(1, 0), c1(0, 1) { (void) tag; }
        generic_mat2(const vec_t &c0, const vec_t &c1) : c0(c0), c1(c1) {};

        generic_mat2(const generic_mat2 &rhs) noexcept = default;
        generic_mat2 &operator=(const generic_mat2 &rhs) noexcept = default;

        inline generic_mat2 operator*(const generic_mat2 &rhs) {
            generic_mat2 ret;
            ret.c0 = c0 * rhs;
            ret.c1 = c1 * rhs;
            return ret;
        }

        inline generic_mat2 &operator*=(const generic_mat2 &rhs) {
            c0 *= rhs; c1 *= rhs; return *this;
        }

        static generic_mat2<T> transform(const generic_vec2<T> &scale, T theta) {
            generic_mat2<T> ret{};

            T cos = std::cos(theta);
            T sin = std::sin(theta);
            ret.c0.x = cos * scale.x;
            ret.c0.y = sin * scale.x;

            ret.c0.x = -sin * scale.y;
            ret.c1.y = cos * scale.y;

            return ret;
        }


    };

    template <typename T>
    class generic_vec2 {
    public:
        T x, y;

        generic_vec2() : x(0), y(0) {};
        generic_vec2(T x, T y) : x(x), y(y) {};
        generic_vec2(const generic_vec2<T> &rhs) noexcept = default;

        inline generic_vec2<T> &operator=(const generic_vec2<T> &rhs) noexcept = default;

        inline generic_vec2<T> operator+(const generic_vec2<T> &rhs) const noexcept {
            return generic_vec2<T>(x + rhs.x, y + rhs.y);
        }

        inline generic_vec2<T> operator-(const generic_vec2<T> &rhs) const noexcept {
            return generic_vec2<T>(x - rhs.x, y - rhs.y);
        }

        inline generic_vec2<T> operator*(const T rhs) const noexcept {
            return generic_vec2<T>(x * rhs, y * rhs);
        };

        inline generic_vec2<T> operator/(const T rhs) const noexcept {
            return generic_vec2<T>(x / rhs, y / rhs);
        }

        inline generic_vec2 &operator+=(const generic_vec2<T> &rhs) noexcept {
            x += rhs.x; y += rhs.y; return *this;
        }

        inline generic_vec2 &operator-=(const generic_vec2<T> &rhs) noexcept {
            x -= rhs.x; y -= rhs.y; return *this;
        }

        inline generic_vec2 &operator*=(const T rhs) noexcept {
            x *= rhs; y *= rhs; return *this;
        }

        inline generic_vec2 &operator/=(const T rhs) noexcept {
            x /= rhs; y /= rhs; return *this;
        }

        inline generic_vec2<T> operator*(const generic_mat2<T> &rhs) const noexcept {
            generic_vec2<T> ret;
            ret += rhs.c0 * x;
            ret += rhs.c1 * y;
            return ret;
        }

        inline generic_vec2 &operator*=(const generic_mat2<T> &rhs) noexcept { *this = *this * rhs; return *this; }

        // DOT PRODUCT!
        inline T operator*(const generic_vec2<T> &rhs) noexcept {
            return x * rhs.x + y * rhs.y;
        }

        inline T magnitude() const {
            return std::sqrt(x * x + y * y);
        }

        inline generic_vec2<T> normalized() const {
            return *this / magnitude();
        }

        inline void normalize_in_place() {
            *this /= magnitude();
        }
    };

    template <typename T>
    class generic_vec3 {
        T x, y, z;
        generic_vec3() : x(0), y(0), z(0) {};
        generic_vec3(T x, T y, T z) : x(x), y(y), z(z) {};
        generic_vec3(const generic_vec3<T> &rhs) noexcept = default;

        generic_vec3<T> &operator=(const generic_vec3<T> &rhs) noexcept = default;
    };

    template <typename T>
    class generic_vec4;

    template <typename T>
    class generic_mat4 {
    private:
        typedef generic_vec4<T> vec_t;
    public:
        // c for "column"
        vec_t c0{}, c1{}, c2{}, c3{};

        generic_mat4() = default;
        explicit generic_mat4(const identity_tag tag) : c0(1, 0, 0, 0), c1(0, 1, 0, 0), c2(0, 0, 1, 0), c3(0, 0, 0, 1) { (void) tag; }
        generic_mat4(const vec_t &c0, const vec_t &c1, const vec_t &c2, const vec_t &c3) : c0(c0), c1(c1), c2(c2), c3(c3) {}
        generic_mat4(const generic_mat4<T> &rhs) noexcept = default;

        generic_mat4<T> &operator=(const generic_mat4<T> &rhs) noexcept = default;

        inline generic_mat4<T> operator*(const generic_mat4<T> &rhs) const noexcept {
            generic_mat4<T> ret;
            ret.c0 = c0 * rhs;
            ret.c1 = c1 * rhs;
            ret.c2 = c2 * rhs;
            ret.c3 = c3 * rhs;
            return ret;
        }

        inline generic_mat4 &operator*=(const generic_mat4<T> &rhs) noexcept {
            c0 *= rhs; c1 *= rhs; c2 *= rhs; c3 *= rhs; return *this;
        }

        /**
         * from GLM https://github.com/g-truc/glm/blob/0.9.5/glm/gtc/matrix_transform.inl
         * https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/gluPerspective.xml
         * @param fovy Vertical FOV in DEGREES
         */
        static generic_mat4<T> perspective(float_t fovy, float_t aspect, float_t zNear, float_t zFar) {
            //EXIF(aspect == 0, "aspect ratio can't be 0")
            //EXIF(zFar == zNear, "Near and far clipping planes must be distinct")

            float_t const f = 1 / std::tan(radians(fovy) / 2); // cotangent

            generic_mat4<T> ret;

            ret.c0.x = f / aspect;
            ret.c1.y = f;
            ret.c2.z = (zFar + zNear) / (zNear - zFar);
            ret.c2.w = -1;
            ret.c3.z = (2 * zFar * zNear) / ( zNear - zFar);
            return ret;
        }

        // https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glOrtho.xml
        static generic_mat4<T> ortho(float_t left, float_t right, float_t bottom, float_t top, float_t zNear, float_t zFar) {
            generic_mat4<T> ret;

            ret.c0.x = 2 / (right - left);
            ret.c1.y = 2 / (top - bottom);
            ret.c2.z = - 2 / (zFar - zNear);
            ret.c3.x = - (right + left) / (right - left);
            ret.c3.y = - (top + bottom) / (top - bottom);
            ret.c3.z = - (zFar + zNear) / (zFar - zNear);
            ret.c3.w = 1;
            return ret;
        }

        static generic_mat4<T> transform(const generic_vec3<T> &translate, const generic_vec2<T> &scale, T theta) {
            generic_mat4<T> ret{identity_tag{}};

            T sin = std::sin(theta);
            T cos = std::cos(theta);
            ret.c0.x = cos * scale.x;
            ret.c0.y = sin * scale.x;

            ret.c1.x = -sin * scale.y; // cos(theta + 90deg) = -cos(90 - theta) = -sin(theta)
            ret.c1.y = cos * scale.y; // sin(theta + 90deg) = sin(90deg - theta) = cos(theta)

            ret.c3.x = translate.x;
            ret.c3.y = translate.y;
            ret.c3.z = translate.z;
            return ret;
        };
    };

    template <typename T>
    class generic_vec4 {
    public:
        T x, y, z, w; // this is out of order, but whatever.

        generic_vec4() : x(0), y(0), z(0), w(0) {};
        generic_vec4(T x, T y, T z, T w) : x(x), y(y), z(z), w(w) {};
        generic_vec4(const generic_vec4<T> &rhs) noexcept = default;

        generic_vec4<T> &operator=(const generic_vec4<T> &rhs) noexcept = default;

        inline generic_vec4<T> operator+(const generic_vec4<T> &rhs) const noexcept {
            return generic_vec4<T>(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w);
        }

        inline generic_vec4<T> operator*(const T rhs) const noexcept {
            return generic_vec4<T>(x * rhs, y * rhs, z * rhs, w * rhs);
        };

        inline generic_vec4 &operator+=(const generic_vec4<T> &rhs) noexcept {
            x += rhs.x; y += rhs.y; z += rhs.z; w += rhs.w; return *this;
        }

        // we transform this vector BY rhs.
        inline generic_vec4<T> operator*(const generic_mat4<T> &rhs) const noexcept {
            generic_vec4<T> ret;
            ret += rhs.c0 * x;
            ret += rhs.c1 * y;
            ret += rhs.c2 * z;
            ret += rhs.c3 * w;
            return ret;
        };

        inline generic_vec4 &operator*=(const generic_mat4<T> &rhs) noexcept { *this = *this * rhs; return *this; }
    };

    typedef generic_vec2<float_t> vec2;
    typedef generic_vec4<float_t> vec4;
    typedef generic_mat4<float_t> mat4;
    typedef generic_mat2<float_t> mat2;
}

#endif