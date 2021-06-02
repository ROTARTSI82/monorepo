//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef GAMETEST_COMMON_H
#define GAMETEST_COMMON_H

#include <stdio.h>
#include <stdlib.h>


#include "gl.h"
#include "file_reader.h"

// EXit IF
#define EXIF(exp, msg) if (exp) { printf(msg); exit(1); }

//void overwrite_file(const char *filename, void *data, size_t len) { // len being length in bytes
//    FILE *fp = fopen(filename, "w");
//
//}

uint64_t reverse_bits(uint64_t in);
uint64_t reverse_bytes(uint64_t in);

typedef struct app_t {
    GLFWwindow *win;
    GLuint quad_vbo;

    GLuint default_shader;
    GLint su_view_mat;
    GLint su_proj_mat;
    GLint su_tex_samp;

    smat4 projection;
    smat4 view_mat;

    GLuint TMP_TEST_TEX;

    render_ctx_t master_ctx;
} app_t;

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

enum compression_chunk_type_t {
    e_node = 'n', // node of huffman tree
    e_leaf = 'l', // leaf node in huffman tree. value is next byte
    e_begin_encoding = 'B',

    e_data = 'd', // 4 byte length. 4gb max. do we even need chunks bigger than 65kb?
    e_datas = 's', //  2 byte length 65kb max
    e_datab = 'b', // 1 byte length 256bytes max
    e_ptr = 'p', // pointer to a e_data or e_datab chunk to insert here.
    e_rep = 'R' // Repeat the last e_data, e_datab, or e_ptr chunk x times
};

typedef struct cmp_head_t {
    char magic[4]; // "RCMP"
    uint32_t version; // big-endian, most sign 16 bits = compat version, least sign 16 bits = micro version.
} cmp_head_t;

// this is so fucking jank
typedef struct huffman_node_t huffman_node_t;
struct huffman_node_t {
    huffman_node_t *c0; // route if 0
    huffman_node_t *c1; // route if 1

    uint8_t value;
};

void *decompress(void *src, size_t *out_size);

// https://stackoverflow.com/questions/4239993/determining-endianness-at-compile-time/4240029
uint8_t is_little_endian();


#endif //GAMETEST_COMMON_H
