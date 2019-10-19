//
// Created by 25granty on 10/17/19.
//

#pragma once

#ifndef MASONSHARE_CL_CORE_H
#define MASONSHARE_CL_CORE_H

#include "mason/log.h"
#include "mason/cl/cl_platform.h"
#include "mason/cl/cl_device.h"

#include "OpenCL/opencl.h"

namespace mason::cl {
    std::vector<mason::cl::cl_platform> get_platforms(cl_uint max_ids);

    std::vector<mason::cl::cl_device> get_devices(cl_platform_id plat, cl_device_type dev_type, cl_uint max_devs);

    std::vector<mason::cl::cl_device>
    get_devices(const mason::cl::cl_platform &plat, cl_device_type dev_type, cl_uint max_devs);
}

#endif //MASONSHARE_CL_CORE_H
