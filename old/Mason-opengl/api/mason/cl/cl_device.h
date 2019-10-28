//
// Created by 25granty on 10/27/19.
//

#pragma once

#ifndef __MASON_CL_DEVICE_H
#define __MASON_CL_DEVICE_H

#include "mason/cl/cl_config.h"

namespace mason::cl {
    class cl_device {
    public:
        std::vector<::cl::Device> devices;
        ::cl::Context context;
        ::cl::CommandQueue queue;

        explicit cl_device(cl_device_type type);

        virtual ~cl_device() = default;
    };
}


#endif //__MASON_CL_DEVICE_H
