#include "csc/PacketFactory.hpp"

#include "csc/varint.hpp"
#include "csc/log.hpp"
#include "csc/misc_defs.hpp"

#include <zlib.h>

namespace csc {
    PacketFactory::PacketFactory(int id, unsigned size) {
        store = new int8_t[size + 10]; // + 10 for 2 var ints: packet id and packet size
        auto end = writeVarInt(id, store);
        auto idSize = end - store;

        headroom = 10 - idSize;

        // copy it over
        for (int i = 1; i <= idSize; i++)
            store[10 - i] = store[idSize - i];
    }
    PacketFactory::~PacketFactory() {
        delete[] store;
    }

    std::pair<int8_t *, unsigned> PacketFactory::construct(int newsize) {
        newsize += 10 - headroom; // account for packet id
        auto end = writeVarInt(newsize, store);
        auto size = end - store;

        for (int i = 1; i <= size; i++)
            store[headroom - i] = store[size - i];

        CEL_INFO("construct() = {}, {}", headroom - size, size + newsize);
        return std::make_pair<int8_t *, unsigned>(store + headroom - size, size + newsize);
    }



    CompressedPacketFactory::CompressedPacketFactory(int id, unsigned size) {
        store = new int8_t[size + 15]; // + 5 for 1 var int: packet id
        auto end = writeVarInt(id, store);
        auto idSize = end - store;

        headroom = 15 - idSize;

        // copy it over
        for (int i = 1; i <= idSize; i++)
            store[15 - i] = store[idSize - i];
    }
    CompressedPacketFactory::~CompressedPacketFactory() {
        delete[] store;
    }

    std::pair<int8_t *, unsigned> CompressedPacketFactory::construct(int newsize) {
        if (newsize > COMPRESSION_THRESHOLD) {
            CEL_INFO("Packet is being compressed");
            int8_t *compressed = new int8_t[newsize + 5];
            uLong cmpSize = newsize + 5;
            auto dataLen = newsize + (15 - headroom);
            if (compress2((Bytef *) compressed, &cmpSize, (Bytef *) store + headroom, dataLen, Z_DEFAULT_COMPRESSION) != Z_OK) {
                CEL_ERROR("zlib compression fail!");
                delete[] compressed;
                return std::make_pair(store, 0U); // dummy payload
            }

            memcpy(store + 10, compressed, cmpSize);
            delete[] compressed;

            // write the Data Length and copy it up to the 10th byte
            auto end = writeVarInt(dataLen, store);
            auto size = end - store;
            for (int i = 1; i <= size; i++)
                store[10 - i] = store[size - i];

            headroom = 10 - size;
            end = writeVarInt(size + dataLen, store); // total packet length
            auto plenSize = end - store;
            for (int i = 1; i <= plenSize; i++)
                store[headroom - i] = store[plenSize - i];

            return std::make_pair<int8_t *, unsigned>(store + headroom - size, size + plenSize + cmpSize);
        }

        CEL_INFO("Packet is NOT compressed");

        // here, headroom is the index of the begin
        CEL_INFO("packet id at {}", headroom);
        store[headroom - 1] = 0; // data length is 0
        headroom--;

        // write packet size and shift it over
        // (15 - headroom) is the size of the packet id
        auto end = writeVarInt(newsize + (15 - headroom), store);
        auto size = end - store;


        for (int i = 1; i <= size; i++)
            store[headroom - i] = store[size - i];

        return std::make_pair<int8_t *, unsigned>(store + headroom - size, size + newsize + (15 - headroom));
    }

    PacketFactory::PacketFactory(PacketFactory &&rhs) {
        *this = std::move(rhs);
    }

    PacketFactory &PacketFactory::operator=(PacketFactory &&rhs) {
        if (this == &rhs) return *this;
        store = rhs.store;
        headroom = rhs.headroom;
        rhs.store = nullptr;
        return *this;
    }

    CompressedPacketFactory::CompressedPacketFactory(CompressedPacketFactory &&rhs) {
        *this = std::move(rhs);
    }

    CompressedPacketFactory &CompressedPacketFactory::operator=(CompressedPacketFactory &&rhs) {
        if (this == &rhs) return *this;
        store = rhs.store;
        headroom = rhs.headroom;
        rhs.store = nullptr;
        return *this;
    }

    VariantPacketFactory::VariantPacketFactory(CompressedPacketFactory &&fac) : cmp(std::move(fac)), isCompressed(true) {}
    VariantPacketFactory::VariantPacketFactory(PacketFactory &&fac) : uncmp(std::move(fac)), isCompressed(false) {}
    VariantPacketFactory::~VariantPacketFactory() {
        if (isCompressed) cmp.~CompressedPacketFactory();
        else uncmp.~PacketFactory();
    }
}