//
// Created by 25granty on 10/18/19.
//

#include "mason/cl/cl_platform.h"

namespace mason::cl {
    cl_platform::cl_platform(cl_platform_id id) : int_id(id) {}

    cl_platform::~cl_platform() {
        cl_int status = clUnloadPlatformCompiler(int_id);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to unload platform compiler for OpenCL platform!");
        }
    }

    void cl_platform::bind() {
        __active_platform = &this->int_id;
    }
}
