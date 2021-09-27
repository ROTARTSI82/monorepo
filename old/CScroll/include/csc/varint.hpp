/**
 * @file varint.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-09-26
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CSC_VARINT_HPP
#define CSC_VARINT_HPP

#include <cstdint>

namespace csc {
    // @param in Pointer to the byte to start reading from.
    //           This pointer will be left pointing to the byte AFTER the last byte consumed.
    int32_t readVarInt(int8_t **in);

    // @param in Pointer to the byte to start reading from.
    //           This pointer will be left pointing to the byte AFTER the last byte consumed.
    int64_t readVarLong(int8_t **in);

    // @return A pointer to the byte AFTER the last byte written
    int8_t *writeVarInt(int32_t value, int8_t *out);

    // @return A pointer to the byte AFTER the last byte written
    int8_t *writeVarLong(int64_t value, int8_t *out);

    void varIntSanityCheck();
}

#endif