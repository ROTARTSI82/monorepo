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

typedef struct {
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

#endif //GAMETEST_COMMON_H
