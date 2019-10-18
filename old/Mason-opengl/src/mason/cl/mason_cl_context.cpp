//
// Created by 25granty on 10/18/19.
//

#include "mason/cl/mason_cl_context.h"

namespace mason::cl {

    mason_cl_context::mason_cl_context(std::vector<cl_context_properties> props, std::vector<cl_device_id> devs,
                                       void (CL_CALLBACK *pfn_notify)(const char *errinfo, const void *private_info,
                                                                      size_t cb, void *user_data),
                                       void *usr_dat) {
        const cl_context_properties *prop_arr = props.empty() ? nullptr : &props[0];
        const cl_device_id *dev_arr = devs.empty() ? nullptr : &devs[0];

        cl_int status = -1;

        int_contex = clCreateContext(prop_arr, devs.size(), dev_arr, pfn_notify, usr_dat, &status);

        if (status != CL_SUCCESS) {
            MASON_WARN("CL context creation failed! This WILL crash!");
        }

    }

    mason_cl_context::~mason_cl_context() {

    }

    void mason_cl_context::bind() {

    }
}
