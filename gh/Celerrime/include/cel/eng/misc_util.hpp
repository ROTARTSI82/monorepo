/**
 * @file misc_util.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-16
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_MISC_UTIL_HPP
#define CEL_ENG_MISC_UTIL_HPP

#include <cstdint>

namespace cel {
    /**
     * @brief Decodes a varaible-width UTF-8 codepoint from a sequence of `char`s.
     *        One call will consume between 1 and 4 `char`s, and 
     *        **NO bounds checking is performed!!**
     * 
     * @param in Points to the first byte to consider as the start of a code point.
     *           Upon returning, points to the byte AFTER the last byte that was used.
     * @return uint32_t Decoded codepoint (big endian https://datatracker.ietf.org/doc/html/rfc3629)
     */
    uint32_t utf8_codepoint_decode(uint8_t **in);

    /**
     * @brief Encodes a code point (big endian, rfc3629). NO BOUNDS CHECKING!
     * 
     * @param in Points to the first byte to start storing the encoded codepoint.
     * @param codepoint Codepoint to encode.
     * @return uint8_t* Points to the byte AFTER the last byte that was encoded.
     */
    uint8_t *utf8_codepoint_encode(uint8_t *in, uint32_t codepoint);

    class raw_stack {
    private:
        uint8_t *store = nullptr;
        uint8_t *sp = nullptr; // stack pointer

    public:
        raw_stack() = delete;
        raw_stack(std::size_t size);
        ~raw_stack();

        raw_stack(const raw_stack &rhs) = delete;
        raw_stack &operator=(const raw_stack &rhs) = delete;

        raw_stack(raw_stack &&rhs) noexcept;
        raw_stack &operator=(raw_stack &&rhs) noexcept;

        inline uint8_t *push(unsigned number) {
            return sp += number;
        }

        inline uint8_t *pop(unsigned number) {
            return sp -= number;
        }

        inline uint8_t *at(unsigned ind) {
            return store + ind;
        }

        inline uint8_t *peek(unsigned back = 1) {
            return sp - back;
        }
    };

}

#endif