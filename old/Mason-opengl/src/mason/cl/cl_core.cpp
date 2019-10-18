//
// Created by 25granty on 10/18/19.
//

#include "mason/cl/cl_core.h"

namespace mason::cl {

    std::vector<cl_platform_id> get_platform_ids(cl_uint max_ids) {
        auto platforms = new cl_platform_id[max_ids];
        cl_uint size = 0;
        cl_int status = clGetPlatformIDs(max_ids, platforms, &size);
        if (status != CL_SUCCESS) {
            MASON_WARN("mason::cl::get_platform_ids() failed! Returning empty");
            return {};
        }
        std::vector<cl_platform_id> platform_vec;

        for (unsigned i = 0; i < size; i++) {
            cl_platform_id plat = *(platforms + i);
            if (plat) {
                platform_vec.emplace_back(plat);
            } else {
                MASON_INFO("Breaking out of platform vector transfer! This shouldn't happen!");
                break;
            }
        }

        delete[] platforms;

        return platform_vec;
    }

    std::vector<cl_device_id> get_device_ids(cl_platform_id plat, cl_device_type dev_type, cl_uint max_devs) {
        auto devices = new cl_device_id[max_devs];
        cl_uint size = 0;
        cl_int status = clGetDeviceIDs(plat, dev_type, max_devs, devices, &size);

        if (status != CL_SUCCESS) {
            MASON_WARN("mason::cl::get_device_ids() failed! Returning empty!");
            return {};
        }
        std::vector<cl_device_id> dev_vec;

        for (unsigned i = 0; i < size; i++) {
            cl_device_id dev = *(devices + i);
            if (dev) {
                dev_vec.emplace_back(dev);
            } else {
                MASON_INFO("Breaking out of device vector transfer! This shouldn't happen!");
                break;
            }
        }

        delete[] devices;

        return dev_vec;
    }
}

