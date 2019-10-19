//
// Created by 25granty on 10/18/19.
//

#pragma once

#ifndef MASONSHARE_CL_DEVICE_H
#define MASONSHARE_CL_DEVICE_H

#include "OpenCL/opencl.h"

#include "mason/log.h"

namespace mason::cl {
    class cl_device {
    public:
        cl_device_id int_id;

        explicit cl_device(cl_device_id id);

        ~cl_device();

        void bind();
    };

    cl_device_id *__active_device = nullptr;
}


#endif //MASONSHARE_CL_DEVICE_H
