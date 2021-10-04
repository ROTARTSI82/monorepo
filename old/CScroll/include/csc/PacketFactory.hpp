/**
 * @file PacketFactory.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-10-02
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CSC_PACKET_FACTORY_HPP
#define CSC_PACKET_FACTORY_HPP

#include <cstdint>
#include <tuple>
#include <variant>

namespace csc {
      class PacketFactory {
    private:
        int8_t *store;
        uint8_t headroom;

    public:
        PacketFactory() = default;
        PacketFactory(int id, unsigned size);
        ~PacketFactory();

        PacketFactory(const PacketFactory &rhs) = delete;
        PacketFactory &operator=(const PacketFactory &rhs) = delete;

        PacketFactory(PacketFactory &&rhs);
        PacketFactory &operator=(PacketFactory &&rhs);

        inline int8_t *get() { return store + 10; }

        std::pair<int8_t *, unsigned> construct(int newsize);
    };

    class CompressedPacketFactory {
    private:
        int8_t *store;
        uint8_t headroom;

    public:
        CompressedPacketFactory() = default;
        CompressedPacketFactory(int id, unsigned size);
        ~CompressedPacketFactory();

        CompressedPacketFactory(const CompressedPacketFactory &rhs) = delete;
        CompressedPacketFactory &operator=(const CompressedPacketFactory &rhs) = delete;

        CompressedPacketFactory(CompressedPacketFactory &&rhs);
        CompressedPacketFactory &operator=(CompressedPacketFactory &&rhs);

        inline int8_t *get() { return store + 15; }

        std::pair<int8_t *, unsigned> construct(int newsize);
    };

    class VariantPacketFactory {
    private:
        union {
            CompressedPacketFactory cmp;
            PacketFactory uncmp;
        };
        bool isCompressed;

    public:
        VariantPacketFactory() = delete;
        VariantPacketFactory(CompressedPacketFactory &&fac);
        VariantPacketFactory(PacketFactory &&fac);
        ~VariantPacketFactory();

        VariantPacketFactory(const VariantPacketFactory &rhs) = delete;
        VariantPacketFactory &operator=(const VariantPacketFactory &rhs) = delete;

        inline int8_t *get() { return isCompressed ? cmp.get() : uncmp.get(); }
        inline std::pair<int8_t *, unsigned> construct(int nsize) { return isCompressed ? cmp.construct(nsize) : uncmp.construct(nsize); }
    };
}

#endif