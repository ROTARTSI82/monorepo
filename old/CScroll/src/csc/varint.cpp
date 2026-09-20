#include "csc/varint.hpp"
#include "csc/log.hpp"

#include <stdexcept>

namespace csc {
    // are these necessary? Technically left-shifting negative signed integers is undefined behavior.
    static inline int32_t arith_shl32(int32_t in, int8_t sh) {
        uint32_t op = *reinterpret_cast<uint32_t *>(&in);
        op <<= sh;
        return *reinterpret_cast<uint32_t *>(&op);
    }

    static inline int64_t arith_shl64(int64_t in, int8_t sh) {
        uint64_t op = *reinterpret_cast<uint64_t *>(&in);
        op <<= sh;
        return *reinterpret_cast<uint64_t *>(&op);
    }


    int32_t readVarInt(int8_t **in) {
        int32_t value = 0;
        int32_t bitOffset = 0;
        int8_t currentByte = 0;
        do {
            // if (bitOffset == 35) throw std::runtime_error("VarInt is too big");

            currentByte = *((*in)++);
            value |= arith_shl32(currentByte & 0b01111111, bitOffset);

            bitOffset += 7;
        } while ((currentByte & 0b10000000) != 0);

        return value;
    }

    int64_t readVarLong(int8_t **in) {
        int64_t value = 0;
        int32_t bitOffset = 0;
        int8_t currentByte = 0;
        do {
            // if (bitOffset == 70) throw std::runtime_error("VarLong is too big");

            currentByte = *((*in)++);
            value |= arith_shl64(currentByte & 0b01111111, bitOffset);

            bitOffset += 7;
        } while ((currentByte & 0b10000000) != 0);

        return value;
    }

    int8_t *writeVarInt(int32_t value, int8_t *out) {
        while (true) {
            if ((value & 0xFFFFFF80) == 0) {
                *out++ = value;
                return out;
            }

            *out++ = (value & 0x7F | 0x80);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            *reinterpret_cast<uint32_t *>(&value) >>= 7; // force a LOGICAL shift as right shifting negative signed integers is implementation-defined
        }
    }

    int8_t *writeVarLong(int64_t value, int8_t *out) {
        while (true) {
            if ((value & 0xFFFFFFFFFFFFFF80) == 0) {
                *out++ = value;
                return out;
            }

            *out++ = (value & 0x7F | 0x80);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            *reinterpret_cast<uint64_t *>(&value) >>= 7; // force a LOGICAL shift as right shifting negative signed integers is implementation-defined
        }
    }

    std::string readString(int8_t **in) {
        int len = readVarInt(in);
        std::string ret{(char *) *in, static_cast<std::string::size_type>(len)};
        *in += len;
        return ret;
    }



    #define SANITY_CASE1i(val, exp) decPtr = (int8_t *) dec0; \
        writeVarInt(val, decPtr); \
        CEL_INFO("dec0 = {:#0x} (Expected " # exp ")", dec0[0]); \
        CEL_INFO("decInt(dec0) = {} (Expected {})", readVarInt(&decPtr), val);

    #define SANITY_CASEiv(val, exp) decPtr = (int8_t *) dec0; \
        end = writeVarInt(val, decPtr); \
        for (uint8_t *it = reinterpret_cast<uint8_t *>(dec0); it != reinterpret_cast<uint8_t *>(end); it++) { \
            CEL_INFO("{:#0x}", *it); \
        } \
        CEL_INFO("Expected: {}", exp); \
        CEL_INFO("decInt(^^^) = {} (Expected {})", readVarInt(&decPtr), val);

    #define SANITY_CASE1l(val, exp) decPtr = (int8_t *) dec0; \
        writeVarLong(val, decPtr); \
        CEL_INFO("dec0 = {:#0x} (Expected " # exp ")", dec0[0]); \
        CEL_INFO("decLong(dec0) = {} (Expected {})", readVarLong(&decPtr), val);

    #define SANITY_CASElv(val, exp) decPtr = (int8_t *) dec0; \
        end = writeVarLong(val, decPtr); \
        for (uint8_t *it = reinterpret_cast<uint8_t *>(dec0); it != reinterpret_cast<uint8_t *>(end); it++) { \
            CEL_INFO("{:#0x}", *it); \
        } \
        CEL_INFO("Expected: {}", exp); \
        CEL_INFO("decLong(^^^) = {} (Expected {})", readVarLong(&decPtr), val);

    void varIntSanityCheck() {
        CEL_INFO("VarInt module sanity check:");
        uint8_t dec0[10];

        int8_t *decPtr = (int8_t *) dec0;
        int8_t *end;
        writeVarInt(0, decPtr);
        CEL_INFO("dec0 = {:#0x} (Expected 0x00)", dec0[0]);
        CEL_INFO("decInt(0x00) = {} (Expected 0)", readVarInt(&decPtr));

        SANITY_CASE1i(1, 0x01);
        SANITY_CASE1i(2, 0x02);
        SANITY_CASE1i(127, 0x7f);

        SANITY_CASEiv(128, "0x80 0x01");
        SANITY_CASEiv(255, "0xff 0x01");
        SANITY_CASEiv(2097151, "0xff 0xff 0x7f");
        SANITY_CASEiv(2147483647, "0xff 0xff 0xff 0xff 0x07");
        SANITY_CASEiv(-1, "0xff 0xff 0xff 0xff 0x0f");
        SANITY_CASEiv(-2147483648, "0x80 0x80 0x80 0x80 0x08");

        SANITY_CASE1l(0, 0x00);
        SANITY_CASE1l(1, 0x01);
        SANITY_CASE1l(2, 0x02);

        SANITY_CASElv(127, "0x7f");
        SANITY_CASElv(128, "0x80 0x01");
        SANITY_CASElv(255, "0xff 0x01");
        SANITY_CASElv(2147483647, "0xff 0xff 0xff 0xff 0x07");
        SANITY_CASElv(9223372036854775807, "0xff 0xff 0xff 0xff 0xff 0xff 0xff 0xff 0x7f");
        SANITY_CASElv(-1, "0xff 0xff 0xff 0xff 0xff 0xff 0xff 0xff 0xff 0x01");
        SANITY_CASElv(-2147483648, "0x80 0x80 0x80 0x80 0xf8 0xff 0xff 0xff 0xff 0x01");
        SANITY_CASElv(-9223372036854775808, "0x80 0x80 0x80 0x80 0x80 0x80 0x80 0x80 0x80 0x01");
    }
}