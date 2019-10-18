//
// Created by 25granty on 10/17/19.
//

#pragma once

#ifndef MASONSHARE_CL_CORE_H
#define MASONSHARE_CL_CORE_H

#include "mason/log.h"
#include "OpenCL/opencl.h"

namespace mason::cl {
    std::vector<cl_platform_id> get_platform_ids(cl_uint max_ids);

    std::vector<cl_device_id> get_device_ids(cl_platform_id plat, cl_device_type dev_type, cl_uint max_devs);
}

#endif //MASONSHARE_CL_CORE_H
