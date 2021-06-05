//
// Created by grant on 5/30/21.
//

#include "file_reader.h"

#include "stdlib.h"
#include <stdio.h>
#include <stdint.h>
#include <memory.h>

#include <arpa/inet.h>
#include "util.h"

void *full_read_file(const char *filename, long *size) {
    FILE *fp = fopen(filename, "rb");
    fseek(fp, 0L, SEEK_END);
    *size = ftell(fp);
    rewind(fp);

    void *ret = malloc((*size));
    fread(ret, 1, (*size), fp);
    fclose(fp);

    return ret;
}

void overwrite_file(const char *filename, void *data, size_t len) {
    FILE *fp = fopen(filename, "wb");
    fwrite(data, 1, len, fp);
    fclose(fp);
}


#define DCMP_ABORT(cond, msg) if (cond) {printf("Error decompressing: "msg"\n"); *out_size = 0; goto abort; }

typedef struct huffman_instruction_t {
    uint8_t type;
    uint8_t value;
} huffman_instruction_t;

typedef struct dcmp_chunk_t {
    uint32_t start; // index into ret, pointing to the first byte
    uint32_t size; // number of bytes
} dcmp_chunk_t;

#define DCMP_COMPAT_VERSION 0x0000
#define DCMP_MICRO_VERSION 0x0000

#define STORAGE_STACK_SIZE 4096

// we should only need at max 512 but *shrug*
#define INSTRUCTION_STACK_SIZE 1024

#define WORK_STACK_SZIE 64

// 16 mb
#define INTER_BUF_SIZE 16000000

#define CHUNK_BUF_SIZE 1024

void *decompress(void *src, size_t *out_size) {
    // TODO: I should probably just scrap this whole routine and go with representing the tree as an array
    // TODO: I never check if I read past the end at any time. i also need to check if bit lengths lie to me
    // TODO: It is never checked if read_head or write_head overrun their buffers

    /*
     * typedef struct node_t {
     *     int index_to_c0;
     *     int index_to_c1; // set to -1 for leaves
     *     uint8_t value; // ignored for nodes
     * } node_t;
     */

    huffman_node_t *storage_stack = 0;
    huffman_node_t *work_stack = 0;
    huffman_instruction_t *instructions = 0;
    uint8_t *inter_buf = 0;
    uint8_t *ret = 0;


    const char magic[] = {'R', 'C', 'M', 'P'};
    DCMP_ABORT(memcmp(magic, src, 4) != 0, "Magic bytes mismatch");

    uint8_t *read_head = (uint8_t *) src + 4;

    {
        uint16_t compat_version = ntohs(*(uint16_t *) read_head);
        read_head += 2;
        uint16_t micro_version = ntohs(*(uint16_t *) read_head);
        read_head += 2;

        DCMP_ABORT(compat_version != DCMP_COMPAT_VERSION, "Compat version mismatched! Decoder is not compatible.");
        if (micro_version > DCMP_MICRO_VERSION) {
            printf("Decompression WARNING: File version is ahead of decoder version.\n");
        } else if (micro_version < DCMP_MICRO_VERSION) {
            printf("Decompression WARNING: File version is behind decoder version.\n");
        }
    }

    DCMP_ABORT(*read_head != e_node, "First chunk was not root of Huffman tree");
    read_head++;

    huffman_node_t root;
    storage_stack = malloc(STORAGE_STACK_SIZE * sizeof(huffman_node_t));
    memset(storage_stack, 0, STORAGE_STACK_SIZE * sizeof(huffman_node_t));
    huffman_node_t *storage_stack_ptr = storage_stack; // stack pointer points to first empty slot

    {
        instructions = malloc(INSTRUCTION_STACK_SIZE * sizeof(huffman_instruction_t));
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

        work_stack = malloc(WORK_STACK_SZIE * sizeof(huffman_node_t));
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
                    DCMP_ABORT(storage_stack_ptr + 2 > storage_stack + STORAGE_STACK_SIZE, "Storage stack overflowed!");
                    DCMP_ABORT(stack_ptr - 2 < work_stack, "Work stack underflowed!");
                    *(storage0) = *(--stack_ptr);
                    *(storage1) = *(--stack_ptr);
                    storage_stack_ptr += 2;

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
        instructions = 0; // prevent double-freeing if we abort later
        work_stack = 0;
    }

    DCMP_ABORT(*(read_head++) != e_begin_encoding, "Encoding does not begin after huffman tree.");

    uint32_t bit_len = ntohl(*(uint32_t *) read_head); // max is ~512 mb
    read_head += 4;


    inter_buf = malloc(INTER_BUF_SIZE); // 16 mb
    uint8_t *write_head = inter_buf;
    huffman_node_t *traverse_head = &root;

    for (uint64_t bit = 0; bit < bit_len; bit++) {
        if (((*(read_head + (bit / 8))) << (bit % 8)) & 128) { // get `bit`th bit of read_head. this *should* work?
            traverse_head = traverse_head->c1;
        } else {
            traverse_head = traverse_head->c0;
        }

        // if this is true, we have encountered a leaf.
        DCMP_ABORT((traverse_head->c0 != 0) ^ (traverse_head->c1 != 0), "Tree node had only 1 child. Tree nodes must have 2 children or none at all.");

        if (!traverse_head->c0) {
            *(write_head++) = traverse_head->value;
            traverse_head = &root;
        }
    }

    DCMP_ABORT(traverse_head != &root, "Stream stopped mid-tree!");
    free(storage_stack);
    storage_stack = 0;

    uint8_t *end_of_inter = write_head;

//    overwrite_file("./res/actual_pre_huffman.bin", inter_buf, end_of_inter - inter_buf);

    *out_size = ntohl(*(uint32_t *) inter_buf); // first bytes should be size of data.
    read_head = inter_buf + 4; // WARNING: This is pretty confusing
    ret = malloc(*out_size);
    write_head = ret;

    uint64_t region_size;
    uint8_t *region_start;

    // this use of goto might be a little evil, but eh
    while (read_head < end_of_inter) {
        uint32_t size;
        uint16_t reps;

        switch (*(read_head++)) {
            case e_data:
                size = ntohl(*(uint32_t *) read_head);
                read_head += 4;
                goto copy;
            case e_datas:
                size = ntohs(*(uint16_t *) read_head);
                read_head += 2;
                goto copy;
            case e_datab:
                size = *(read_head++);
                goto copy;

            case e_ptr:
                region_start = ret + ntohl(*(uint32_t *) read_head); read_head += 4;
                region_size = ntohl(*(uint32_t *) read_head); read_head += 4;

                memcpy(write_head, region_start, region_size);
                write_head += region_size;
                continue; // skip the copy routine

            case e_rep_large:
                // reps do not get added to the chunk stack
                reps = ntohl(*(uint32_t *) read_head); // this is the number of reps, but we'll use the same var
                read_head += 4;
                goto repeat;
            case e_rep_med:
                reps = ntohs(*(uint16_t *) read_head);
                read_head += 2;
                goto repeat;
            case e_rep_small:
                reps = *(read_head++);
                goto repeat;

            default:
                DCMP_ABORT(1, "Unrecognized chunk within huffman encoded section");
        }

    repeat:
        for (uint32_t i = 0; i < reps; i++) {
            memcpy(write_head, region_start, region_size);
            write_head += region_size;
        }

        continue; // do not fall through into the copy routine
    copy:
        region_size = size;
        region_start = write_head;

        memcpy(write_head, read_head, size);
        write_head += size;
        read_head += size;
    }
//    printf("Decompress DEBUG: Expected %lu, actual %lu\n", *out_size, (write_head - ret));
    DCMP_ABORT((unsigned long) (write_head - ret) != *out_size, "Size mismatch!");

    free(storage_stack);
    free(inter_buf);

    return ret;

abort:
    free(instructions);
    free(work_stack);

    free(storage_stack);
    free(inter_buf);
    free(ret);
    return 0;

}
