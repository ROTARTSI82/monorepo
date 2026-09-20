//
// Created by 25granty on 11/3/19.
//

#include "mason/mem_stack.h"

namespace mason {
    mem_stack::mem_stack(std::size_t max_size) : capacity(max_size) {
        mem_pool = new char[max_size];
        stack_ptr = mem_pool;
    }

    mem_stack::~mem_stack() {
        delete[] mem_pool;
    }

    std::size_t mem_stack::get_alloced_mem() {
        return stack_ptr - mem_pool;
    }

    int mem_stack::pop(std::size_t bytes) {
        if ((stack_ptr - bytes) < mem_pool) {
            return 11; // Segfault: underflow
        }
        stack_ptr -= bytes;
        return 0;
    }

    void *mem_stack::push(std::size_t bytes) {
        void *ret = stack_ptr;
        if ((stack_ptr + bytes) > (mem_pool + capacity)) {
            return nullptr; // Overflow
        }

        stack_ptr += bytes;
        return ret;
    }

    void mem_stack::reset() {
        stack_ptr = mem_pool;
    }
}
