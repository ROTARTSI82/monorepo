//
// Created by 25granty on 10/19/19.
//

#include "mason/cl/cl_queue.h"
#include "mason/cl/cl_context.h"
#include "mason/cl/cl_buffer.h"

namespace mason::cl {

    cl_buffer::cl_buffer(cl_mem_flags flags, size_t size, void *data) {

        if (mason::cl::__active_context == nullptr || mason::cl::__active_cmd_queue == nullptr) {
            MASON_FATAL(
                    "Tried to create OpenCL buffer without a bound OpenCL context or OpenCL command queue! This WILL crash!");
            return;
        }

        cl_int status = -1;
        int_buf = clCreateBuffer(*mason::cl::__active_context, flags, size, data, &status);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to create OpenCL buffer!");
        }
    }

    cl_buffer::~cl_buffer() {
        cl_int status = clReleaseMemObject(int_buf);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to release an OpenCL mem object!");
        }
    }

    void cl_buffer::write(size_t offset, size_t size, const void *dat) {
        if (mason::cl::__active_cmd_queue == nullptr) {
            MASON_FATAL("Tried to write to a OpenCL buffer without a OpenCL command queue! Aborting operation!");
            return;
        }

        cl_int status = clEnqueueWriteBuffer(*mason::cl::__active_cmd_queue, int_buf, CL_TRUE, offset, size, dat, 0,
                                             nullptr, nullptr);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to write to OpenCL buffer!");
        }
    }

    template<typename T>
    std::vector<T> cl_buffer::read(size_t offset, size_t num_elements) {
        if (mason::cl::__active_cmd_queue == nullptr) {
            MASON_FATAL("Tried to read to a OpenCL buffer without a OpenCL command queue! Aborting operation!");
            return {};
        }

        std::vector<T> ret(num_elements);  // Do we do this or do we use reserve()?

        cl_int status = clEnqueueReadBuffer(*mason::cl::__active_cmd_queue, int_buf, CL_TRUE, offset,
                                            num_elements * sizeof(T), &ret[0], 0, nullptr, nullptr);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to read OpenCL buffer!");
            return {};
        }

        return ret;
    }

    template<typename T>
    cl_buffer::cl_buffer(cl_mem_flags flags, std::vector<T> data) {
        if (mason::cl::__active_context == nullptr || mason::cl::__active_cmd_queue == nullptr) {
            MASON_FATAL(
                    "Tried to create OpenCL buffer without a bound OpenCL context or OpenCL command queue! This WILL crash!");
            return;
        }

        cl_int status = -1;
        int_buf = clCreateBuffer(*mason::cl::__active_context, flags, data.size() * sizeof(T),
                                 data.empty() ? nullptr : &data[0], &status);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to create OpenCL buffer!");
        }
    }

    template<typename T>
    void cl_buffer::write(size_t offset, std::vector<T> data) {
        if (mason::cl::__active_cmd_queue == nullptr) {
            MASON_FATAL("Tried to write to a OpenCL buffer without a OpenCL command queue! Aborting operation!");
            return;
        }

        cl_int status = clEnqueueWriteBuffer(*mason::cl::__active_cmd_queue, int_buf, CL_TRUE, offset,
                                             data.size() * sizeof(T), &data[0], 0, nullptr, nullptr);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to write to OpenCL buffer!");
        }
    }
}
