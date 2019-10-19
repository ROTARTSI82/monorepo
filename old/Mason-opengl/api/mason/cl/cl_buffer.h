
//
// Created by 25granty on 10/19/19.
//

#pragma once

#ifndef MASONSHARE_CL_BUFFER_H
#define MASONSHARE_CL_BUFFER_H

#include "OpenCL/opencl.h"
#include <vector>
#include "mason/log.h"

namespace mason::cl {
    class cl_buffer {
    public:
        cl_mem int_buf;

        template<typename T>
        cl_buffer(cl_mem_flags flags, std::vector<T> data);

        cl_buffer(cl_mem_flags flags, size_t size, void *data = nullptr);

        ~cl_buffer();

        void write(size_t offset, size_t size, const void *dat);

        template<typename T>
        void write(size_t offset, std::vector<T> data);

        template<typename T>
        std::vector<T> read(size_t offset, size_t num_elements);
    };
}


#endif //MASONSHARE_CL_BUFFER_H
