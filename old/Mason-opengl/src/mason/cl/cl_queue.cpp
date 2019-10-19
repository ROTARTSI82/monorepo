//
// Created by 25granty on 10/18/19.
//

#include "mason/cl/cl_queue.h"

namespace mason::cl {

    cl_queue::cl_queue(mason::cl::cl_context cont, cl_device_id dev, cl_command_queue_properties props) {
        cl_int status = -1;
        int_queue = clCreateCommandQueue(cont.int_contex, dev, props, &status);

        if (status != CL_SUCCESS) {
            MASON_WARN("OpenCL command queue creation failed! This MIGHT crash!");
        }
    }

    cl_queue::cl_queue(_cl_context *cont, cl_device_id dev, cl_command_queue_properties props) {
        cl_int status = -1;
        int_queue = clCreateCommandQueue(cont, dev, props, &status);

        if (status != CL_SUCCESS) {
            MASON_WARN("OpenCL command queue creation failed! This MIGHT crash!");
        }
    }

    cl_queue::~cl_queue() {
        flush();
        sync();

        cl_int status = clReleaseCommandQueue(int_queue);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to release OpenCL command queue!");
        }
    }

    void cl_queue::flush() {
        cl_int status = clFlush(int_queue);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to flush OpenCL command queue!");
        }
    }

    void cl_queue::sync() {
        cl_int status = clFinish(int_queue);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to finish/sync OpenCL command queue!");
        }
    }

    void cl_queue::bind() {
        __active_cmd_queue = &this->int_queue;
    }
}
