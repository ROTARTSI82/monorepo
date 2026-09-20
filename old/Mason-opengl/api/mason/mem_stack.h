//
// Created by 25granty on 11/3/19.
//

#pragma once

#ifndef __MASON_MEM_STACK_H
#define __MASON_MEM_STACK_H

#include <cstdio>

namespace mason {
    class mem_stack {
    public:
        std::size_t capacity;
        char *stack_ptr;
        char *mem_pool;

        explicit mem_stack(std::size_t max_size);
        ~mem_stack();

        std::size_t get_alloced_mem();

        int pop(std::size_t bytes);

        void reset();

        void *push(std::size_t bytes);
    };
}


#endif //__MASON_MEM_STACK_H
