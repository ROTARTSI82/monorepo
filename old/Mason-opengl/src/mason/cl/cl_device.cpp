//
// Created by 25granty on 10/18/19.
//

#include "mason/cl/cl_device.h"

namespace mason::cl {
    cl_device::cl_device(cl_device_id id) : int_id(id) {}

    cl_device::~cl_device() {
        cl_int status = clReleaseDevice(int_id);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to release OpenCL device!");
        }
    }

    void cl_device::bind() {
        __active_device = &this->int_id;
    }
}
