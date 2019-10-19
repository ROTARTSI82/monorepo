//
// Created by 25granty on 10/18/19.
//

#include "mason/cl/cl_context.h"

namespace mason::cl {

    __mason_cl_context::__mason_cl_context(std::vector<cl_context_properties> props, std::vector<cl_device_id> devs,
                                           void (CL_CALLBACK *pfn_notify)(const char *errinfo, const void *private_info,
                                                                      size_t cb, void *user_data),
                                           void *usr_dat) {
        const cl_context_properties *prop_arr = props.empty() ? nullptr : &props[0];
        const cl_device_id *dev_arr = devs.empty() ? nullptr : &devs[0];

        cl_int status = -1;

        int_contex = clCreateContext(prop_arr, devs.size(), dev_arr, pfn_notify, usr_dat, &status);

        if (status != CL_SUCCESS) {
            MASON_WARN("OpenCL context creation failed! This WILL crash!");
        }
    }

    __mason_cl_context::~__mason_cl_context() {
        cl_int status = clReleaseContext(int_contex);

        if (status != CL_SUCCESS) {
            MASON_WARN("Failed to release OpenCL context!");
        }
    }

    __mason_cl_context::__mason_cl_context(std::vector<cl_context_properties> props,
                                           const std::vector<mason::cl::cl_device> &devs,
                                           void (*pfn_notify)(const char *, const void *, size_t, void *),
                                           void *usr_dat) {
        const cl_device_id *dev_arr = nullptr;

        std::vector<cl_device_id> ids; // Allocate outside to avoid risking the pointer going bad.
        if (!devs.empty()) {
            ids.reserve(devs.size());
            for (const auto &dev : devs) {
                ids.emplace_back(dev.int_id);
            }
            dev_arr = &ids[0];
        }

        const cl_context_properties *prop_arr = props.empty() ? nullptr : &props[0];

        cl_int status = -1;

        int_contex = clCreateContext(prop_arr, devs.size(), dev_arr, pfn_notify, usr_dat, &status);

        if (status != CL_SUCCESS) {
            MASON_WARN("OpenCL context creation failed! This WILL crash!");
        }

    }

    void __mason_cl_context::bind() {
        __active_context = &this->int_contex;
    }
}
