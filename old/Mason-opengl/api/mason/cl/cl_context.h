//
// Created by 25granty on 10/18/19.
//

#pragma once

#ifndef MASONSHARE_CL_CONTEXT_H
#define MASONSHARE_CL_CONTEXT_H

#include "OpenCL/opencl.h"
#include "mason/log.h"
#include "mason/cl/cl_device.h"

#include <vector>

namespace mason::cl {

    // Has to be called __mason_cl_context because otherwise it would conflict with the actual OpenCL type.
    class __mason_cl_context {
    public:
        cl_context int_contex;

        explicit __mason_cl_context(std::vector<cl_context_properties> props = {}, std::vector<cl_device_id> devs = {},
                                    void (CL_CALLBACK *pfn_notify)(const char *errinfo, const void *private_info, size_t cb,
                                                        void *user_data) = nullptr,
                                    void *usr_dat = nullptr);

        explicit __mason_cl_context(std::vector<cl_context_properties> props = {},
                                    const std::vector<mason::cl::cl_device> &devs = {},
                                    void (CL_CALLBACK *pfn_notify)(const char *errinfo, const void *private_info,
                                                                   size_t cb,
                                                                   void *user_data) = nullptr,
                                    void *usr_dat = nullptr);

        ~__mason_cl_context();

        void bind();
    };

    cl_context *__active_context = nullptr;

    typedef __mason_cl_context cl_context; // Temporary solution.
}


#endif //MASONSHARE_CL_CONTEXT_H
