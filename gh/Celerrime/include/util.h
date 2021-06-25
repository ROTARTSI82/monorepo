//
// Created by grant on 6/2/21.
//

#pragma once

#ifndef CEL_UTIL_H
#define CEL_UTIL_H

#if defined(_MSC_VER) && (_MSC_VER < 1600)
typedef unsigned char uint8_t;
typedef unsigned int uint32_t;
typedef unsigned __int64 uint64_t;
// Other compilers
#else	// defined(_MSC_VER)
#include <stdint.h>
#endif // !defined(_MSC_VER)

#include <time.h>

typedef struct fps_limiter_t {
    struct timespec last_tick;
    uint64_t target_frametime;
} fps_limiter_t;

void init_fps_limiter(fps_limiter_t *limiter, uint64_t target_frametime);
double timespec_to_sec(struct timespec *spec);
void tick_fps_limiter(fps_limiter_t *limiter);

// https://github.com/aappleby/smhasher/blob/master/src/MurmurHash2.h

uint64_t MurmurHash64A      ( const void * key, int len, uint64_t seed );


uint64_t reverse_bits(uint64_t in);
uint64_t reverse_bytes(uint64_t in);

extern uint8_t rand_table[256];

// Applies 1 round of XORSHIFT: https://en.wikipedia.org/wiki/Xorshift

/**
 * Supposedly has a period of 2^64-1.
 * Bits are pretty evenly distributed
 */
uint64_t rand_nextl(uint64_t rng);

/**
 * Period of ~14. Certain seeds have periods of 7 or 2, but those are rare.
 * Bits not very evenly distributed.
 */
uint8_t rand_nextb(uint8_t x);

/**
 * This is probably the one we're gonna use for rng. Period ~ 9765. Bits are extremely evenly distributed.
 * There are some seeds with EXTREMELY short periods (in the 100s. e.g.: 21941 has a period of 315).
 */
uint16_t rand_nexts(uint16_t x);

/**
 * Supposedly has period of 2^32-1
 * Bits are pretty evenly distributed.
 */
uint32_t rand_nexti(uint32_t rng);

// https://stackoverflow.com/questions/4239993/determining-endianness-at-compile-time/4240029
uint8_t is_little_endian();



#endif //CEL_UTIL_H
