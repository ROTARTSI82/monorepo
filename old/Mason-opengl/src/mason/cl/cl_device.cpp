//
// Created by 25granty on 10/27/19.
//

#include "mason/cl/cl_device.h"

namespace mason::cl {

    cl_device::cl_device(cl_device_type type) {
        context = ::cl::Context(type);
        devices = context.getInfo<CL_CONTEXT_DEVICES>();

        queue = ::cl::CommandQueue(context);
    }
}

