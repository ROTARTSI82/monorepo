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
#include <stdexcept>
#include <exception>
#include <string>
#include <chrono>

namespace cel {
    /**
     * @brief Tries to open and read an entire file into a string.
     * 
     * @param filepath File to open
     * @return std::string String containing the binary contents of the file (unaltered)
     * @throws std::runtime_error If file opening/reading fails.
     */
    std::string read_entire_file(const std::string &filepath);

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

    class fps_limiter {
    private:
        std::chrono::steady_clock::time_point prev_tick;
        uint64_t target_frametime_nano = 1000000000 / 60;
        float fps, target_fps = 60;
    public:
        fps_limiter() = default;
        fps_limiter(float target);
        void tick();

        inline void set_target_fps(float nval) { target_frametime_nano = 1000000000 / nval; target_fps = nval; }
        inline float get_fps() { return fps; };
    };

    template <typename T>
    class counting_ref {
    private:
        T *value;
    public:
        counting_ref(const T *v) : value(const_cast<T *>(v)) {
            if (value) value->inc_ref();
        }

        virtual ~counting_ref() {
            if (value) value->dec_ref();
        }

        counting_ref(const counting_ref &rhs) : value(rhs.value) {
            if (value) value->inc_ref();
        }

        counting_ref(counting_ref &&rhs) : value(rhs.value) {
            rhs.value = nullptr;
        }

        counting_ref &operator=(const counting_ref &rhs) {
            value = rhs.value;
            if (value) value->inc_ref();
        }

        counting_ref &operator=(counting_ref &&rhs) {
            value = rhs.value;
            rhs.value = nullptr;
        }

        inline T *operator->() {
            return value;
        };
    };

    class raw_stack {
    private:
        std::size_t capacity = 0;
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

        inline ptrdiff_t size() {
            return sp - store;
        }
    };


    template <typename T>
    class checked_array {
    private:
        T *store;
        size_t size;
        bool do_delete = false;

    public:

        checked_array() = delete;
        checked_array(T *arr, size_t size) : store(arr), size(size) {};
        checked_array(size_t size) : store(new T[size]), size(size), do_delete(true) {};
        ~checked_array() {
            if (do_delete) delete[] store;
        }

        checked_array(const checked_array &) = delete;
        checked_array &operator=(const checked_array &) = delete;

        checked_array(checked_array &&rhs) noexcept {
            *this = std::move(rhs);
        }

        checked_array &operator=(checked_array &&rhs) noexcept {
            if (this == &rhs) return *this;
            
            store = rhs.store;
            size = rhs.size;
            do_delete = rhs.do_delete;

            rhs.do_delete = false;
            rhs.size = 0;
            rhs.store = nullptr;

            return *this;
        }

        // operator[] has no bounds checking, but at() does.
        // operator+ has no bounds checking, but range() does.
        inline T &operator[](size_t index) {
            return store[index];
        }

        inline T &at(size_t index) {
            if (index >= size) throw std::out_of_range("Checked array out of bounds");
            return store[index];
        }

        /**
         * @brief Checks bounds of accessing a range 
         * 
         * @throws std::out_of_range if the range goes out of bounds
         * @param start Index of the first element to return
         * @param range Number of elements in the range
         * @return T* Pointer to the first element in the range
         */
        inline T *range(size_t start, unsigned span = 1) {
            if ((start + span) > size) throw std::out_of_range("Checked array + out of bounds");
            return store + start;
        }

        inline T *operator+(size_t size) {
            return store + size;
        }
    };

}

#endif