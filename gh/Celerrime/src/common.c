//
// Created by grant on 5/30/21.
//

#include <memory.h>
#include "common.h"

// DOOM RNG!!
uint8_t rand_table[256] = {0x6f, 0x14, 0x73, 0xe0, 0x27, 0x66, 0xe6, 0x56, 0x00, 0x75, 0x3c, 0x05, 0xa6, 0x0e, 0x69,
        0x71, 0xbf, 0x94, 0xd6, 0xa3, 0x4e, 0xf6, 0xef, 0x6e, 0x3c, 0x99, 0x1f, 0x09, 0x8e, 0x67,
        0x40, 0x48, 0xf4, 0x98, 0x43, 0xb1, 0xfc, 0x3e, 0x22, 0x34, 0x54, 0xf6, 0x88, 0xe3, 0x22,
        0x52, 0x6f, 0xff, 0x96, 0x27, 0x5d, 0x5a, 0x7a, 0x24, 0x77, 0xb0, 0x49, 0x17, 0xd2, 0x9e,
        0x5f, 0x42, 0xe6, 0xe3, 0xa2, 0x68, 0xc8, 0x24, 0xe6, 0x23, 0x1f, 0x31, 0x47, 0xfb, 0xab,
        0xc3, 0x6a, 0xb2, 0xb6, 0x1c, 0x14, 0x54, 0x27, 0xeb, 0x41, 0x2d, 0xb1, 0x42, 0x4d, 0x64,
        0x79, 0xb4, 0x76, 0xe2, 0x0b, 0x9a, 0x1f, 0xf1, 0xd5, 0x80, 0xb9, 0x67, 0xed, 0x6c, 0x1a,
        0xa8, 0xb7, 0x79, 0xd3, 0x4c, 0xc7, 0xc2, 0xa9, 0x32, 0x99, 0x1f, 0x2b, 0xeb, 0xb5, 0x4d,
        0x30, 0xed, 0xb5, 0x81, 0x17, 0xe4, 0x72, 0xb1, 0x18, 0xc7, 0x03, 0x39, 0x87, 0xb8, 0x42,
        0x41, 0xc8, 0xd3, 0xb0, 0x5a, 0x74, 0x2d, 0x76, 0x91, 0x7f, 0x4c, 0x31, 0x49, 0x67, 0x4b,
        0xd1, 0x8a, 0xff, 0x3f, 0xd7, 0xbe, 0xe1, 0xf5, 0x2c, 0x7f, 0x45, 0x1c, 0x68, 0x57, 0x30,
        0x49, 0x5d, 0xf6, 0xe7, 0x50, 0xb0, 0xd0, 0x5e, 0xcf, 0xb2, 0x6b, 0x7f, 0x88, 0x95, 0x3f,
        0x06, 0xf0, 0xf7, 0x16, 0xe6, 0x89, 0x53, 0xf5, 0x54, 0x92, 0x34, 0x0d, 0xc5, 0x7c, 0xd2,
        0xf5, 0xa8, 0xd2, 0x0a, 0x00, 0x3c, 0x0d, 0xcc, 0x8c, 0x4a, 0xe7, 0x80, 0xae, 0x8b, 0xb3,
        0x55, 0x4f, 0x51, 0x3a, 0xa9, 0x35, 0x4e, 0x71, 0x6c, 0x75, 0x78, 0x3f, 0x96, 0x77, 0x38,
        0xd5, 0x97, 0x2b, 0x65, 0x02, 0xd3, 0x09, 0x10, 0x8c, 0x62, 0x66, 0xf8, 0xdb, 0xd7, 0xf0,
        0x09, 0x95, 0x28, 0x54, 0xb8, 0x10, 0xa5, 0x9f, 0xc4, 0xd1, 0x6c, 0x74, 0xe1, 0xfe, 0x99, 0x19};

uint64_t reverse_bits(uint64_t in) {
    uint64_t out = 0;
    for (int i = 0; i < 64; i++) {
        out <<= 1;
        out |= in & 1;
        in >>= 1;
    }
    return out;
}

uint64_t reverse_bytes(uint64_t in) {
    uint64_t out = 0;
    for (int i = 0; i < 8; i++) {
        out <<= 8;
        out |= in & 0xFF;
        in >>= 8;
    }

    return out;
}

#define RNG_A 0xe925a051f6aad614
#define RNG_B 0xba2b482d0c4ac7dc
#define RNG_C 0x5c27d496d1868dc8

uint64_t rand_nextl(uint64_t x) {
    x ^= x >> 12;
    x ^= x << 25;
    x ^= x >> 27;
    return x;
}

uint8_t rand_nextb(uint8_t x) {
    x ^= x << 3;
    x ^= x >> 4;
    x ^= x << 1;
    return x;
}

uint16_t rand_nexts(uint16_t x) {
    x ^= x << 7;
    x ^= x >> 9;
    x ^= x << 3;
    return x;
}

uint32_t rand_nexti(uint32_t x) {
    x ^= x << 13;
    x ^= x >> 17;
    x ^= x << 5;
    return x;
}

uint8_t is_little_endian() {
    short int number = 0x1;
    char *numPtr = (char*)&number;
    return (numPtr[0] == 1);
}

#define DCMP_ABORT(cond, msg) if (cond) {printf("Error decompressing: "msg"\n"); *out_size = 0; return NULL; }

typedef struct huffman_instruction_t {
    uint8_t type;
    uint8_t value;
} huffman_instruction_t;

#define STORAGE_STACK_SIZE 4096

// we should only need at max 512 but *shrug*
#define INSTRUCTION_STACK_SIZE 1024

#define WORK_STACK_SZIE 64

// 16 mb
#define INTER_BUF_SIZE 16000000

void *decompress(void *src, size_t *out_size) {
    printf("ENTER DCMP\n");
    // TODO: I should probably just scrap this whole routine and go with representing the tree as an array
    // TODO: I never check if I read past the end at any time. i also need to check if bit lengths lie to me

    /*
     * typedef struct node_t {
     *     int index_to_c0;
     *     int index_to_c1; // set to -1 for leaves
     *     uint8_t value; // ignored for nodes
     * } node_t;
     */

    const char magic[] = {'R', 'C', 'M', 'P'};
    DCMP_ABORT(memcmp(magic, src, 4) != 0, "Magic bytes mismatch");

    uint8_t *read_head = (uint8_t *) src + 4;
    read_head += 4; // ignore version bytes for now

    DCMP_ABORT(*read_head != e_node, "First chunk was not root of Huffman tree");
    read_head++;

    huffman_node_t root;
    huffman_node_t *storage_stack = malloc(STORAGE_STACK_SIZE * sizeof(huffman_node_t));
    memset(storage_stack, 0, STORAGE_STACK_SIZE * sizeof(huffman_node_t));
    huffman_node_t *storage_stack_ptr = storage_stack; // stack pointer points to first empty slot

    {
        huffman_instruction_t *instructions = malloc(INSTRUCTION_STACK_SIZE * sizeof(huffman_instruction_t));
        huffman_instruction_t *instruction_pointer = instructions;
        memset(instructions, 0, INSTRUCTION_STACK_SIZE * sizeof(huffman_instruction_t)); // we don't need this.
        instructions[0].type = e_node; // root node

        // unpack the chunks. necessary because node chunks have no value field.
        while (*read_head == e_leaf || *read_head == e_node) {
            huffman_instruction_t *val = ++instruction_pointer; // to skip over the root node at the bottom

            DCMP_ABORT(instruction_pointer > instructions + INSTRUCTION_STACK_SIZE, "Instructions stack overflowed!");

            val->type = *(read_head++);
            if (val->type == e_leaf) {
                val->value = *(read_head++);
            }
        }

        huffman_node_t *work_stack = malloc(WORK_STACK_SZIE * sizeof(huffman_node_t));
        memset(work_stack, 0, WORK_STACK_SZIE * sizeof(huffman_node_t));
        huffman_node_t *stack_ptr = work_stack; // stack pointer points to first empty slot

        while (instruction_pointer >= instructions) {
            huffman_instruction_t *val = instruction_pointer--;

            huffman_node_t to_push;
            huffman_node_t *storage0 = storage_stack_ptr, *storage1 = storage_stack_ptr + 1;

            switch (val->type) {
                case e_leaf:
                    // push a leaf to the stack
                    stack_ptr->value = val->value;
                    stack_ptr->c0 = 0;
                    stack_ptr->c1 = 0;
                    stack_ptr++;

                    DCMP_ABORT(stack_ptr > work_stack + WORK_STACK_SZIE, "Work stack overflowed!");
                    break;
                case e_node:
                    // pop two values from the stack and enter them into permanent storage
                    *(storage0) = *(--stack_ptr);
                    *(storage1) = *(--stack_ptr); // this can write out of bounds but eh
                    storage_stack_ptr += 2;
                    DCMP_ABORT(storage_stack_ptr > storage_stack + STORAGE_STACK_SIZE, "Storage stack overflowed!");
                    DCMP_ABORT(stack_ptr < work_stack, "Work stack underflowed!");

                    // Order is reversed when we transfer it into the instruction stack and reversed
                    // again when we transfer it to the work stack, so we don't reverse it here.
                    to_push.c0 = storage0;
                    to_push.c1 = storage1;
                    to_push.value = 0;

                    *(stack_ptr++) = to_push; // this should never overflow since we just popped 2 items
                    break;

                default:
                    DCMP_ABORT(1, "Unrecognized huffman instruction type!");
            }
        };

        // at the end, the root node should be the only item left in the work stack
        DCMP_ABORT(stack_ptr != (work_stack + 1), "Huffman tree has no root node‽");
        root = work_stack[0];

        free(instructions);
        free(work_stack);
    }

    DCMP_ABORT(*(read_head++) != e_begin_encoding, "Encoding does not begin after huffman tree.");

    uint64_t bit_len = 0;
    if (is_little_endian()) {
        // the compiler should be smart enough to optimize this lol. prolly shouldn't count on that.
        for (int i = 0; i < 8; i++) {
            bit_len <<= 8;
            bit_len |= *(read_head + i);
        }
    } else {
        bit_len = *(uint64_t *) read_head;
    }

    printf("Got bit length of %lu. cast would return %lu\n", bit_len, *(uint64_t *) read_head);
    read_head += 8;


    uint8_t *inter_buf = malloc(INTER_BUF_SIZE); // 16 mb
    uint8_t *write_head = inter_buf;
    huffman_node_t *traverse_head = &root;

    for (uint64_t bit = 0; bit < bit_len; bit++) {
        if (((*(read_head + (bit / 8))) << (bit % 8)) & 128) { // get `bit`th bit of read_head. this *should* work?
            traverse_head = traverse_head->c1;
        } else {
            traverse_head = traverse_head->c0;
        }

        // if this is true, we have encountered a leaf.
        if (!(traverse_head->c0 && traverse_head->c1)) { // DeMorgan's law on (!c0) || (!c1)
            *(write_head++) = traverse_head->value;
            traverse_head = &root;
        }
    }

    DCMP_ABORT(traverse_head != &root, "Stream stopped mid-tree!");

    *out_size = write_head - inter_buf;
    read_head = inter_buf; // WARNING: This is pretty confusing

    // TEMPORARY: Null terminate and print
    {
        printf("Out size = %lu\n", *out_size);
        *write_head = '\0';
        printf("Contents = %s\n", inter_buf);

    }

    uint8_t *ret = malloc(*out_size);
    write_head = ret;


    free(storage_stack);
    free(inter_buf);
    return ret;


}

