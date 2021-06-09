//
// Created by grant on 5/30/21.
//

#pragma once

#ifndef GAMETEST_FILE_READER_H
#define GAMETEST_FILE_READER_H

#include <stdint.h>
#include <stddef.h>

// https://github.com/madler/zlib/blob/cacf7f1d4e3d44d871b605da3b647f07d718623f/zlib.h#L1242

void *full_read_file(const char *filename, long *size);

void overwrite_file(const char *filename, void *data, size_t len);

enum compression_chunk_type_t {
    e_node = 'n', // node of huffman tree
    e_leaf = 'l', // leaf node in huffman tree. value is next byte
    e_begin_encoding = 'B',

    e_data = 'd', // 4 byte length. 4gb max. do we even need chunks bigger than 65kb?
    e_datas = 's', //  2 byte length 65kb max
    e_datab = 'b', // 1 byte length 256bytes max

    e_ptr = 'p', // pointer to decoded data. 32-bit index, 32-bit size. this wastes a lot of space lol

    e_dist_len_enc = 'D', // 32-bit distance, 32-bit length

    e_rep_large = 'R', // Repeat the last referenced region. 32-bit number of repetitions
    e_rep_med = 'm', // 16-bit number of repetitions
    e_rep_small = 'r' // 8-bit number of repetitions
};

typedef struct cmp_head_t {
    char magic[4]; // "RCMP"
    uint32_t version; // big-endian, most sign 16 bits = compat version, least sign 16 bits = micro version.
} cmp_head_t;

typedef struct huffman_node_t huffman_node_t;
struct huffman_node_t {
    huffman_node_t *c0; // route if 0
    huffman_node_t *c1; // route if 1

    uint8_t value;
};

// this function is very insecure.
void *decompress(void *src, size_t *out_size);


#endif //GAMETEST_FILE_READER_H
