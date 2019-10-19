//
// Created by 25granty on 10/18/19.
//

#pragma once

#ifndef MASONSHARE_CL_QUEUE_H
#define MASONSHARE_CL_QUEUE_H

#include "OpenCL/opencl.h"

#include "mason/cl/cl_context.h"

namespace mason::cl {
    class cl_queue {
    public:
        cl_command_queue int_queue;

        cl_queue(mason::cl::cl_context cont, cl_device_id dev, cl_command_queue_properties props);

        cl_queue(_cl_context *cont, cl_device_id dev, cl_command_queue_properties props);

        ~cl_queue();

        void bind();

        void flush();  // Makes sure all devices receive their commands
        void sync();  // Blocks until everything finishes executing.
    };

    cl_command_queue *__active_cmd_queue = nullptr;
}


#endif //MASONSHARE_CL_QUEUE_H
