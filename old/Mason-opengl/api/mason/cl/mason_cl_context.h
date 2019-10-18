//
// Created by 25granty on 10/18/19.
//

#pragma once

#ifndef MASONSHARE_MASON_CL_CONTEXT_H
#define MASONSHARE_MASON_CL_CONTEXT_H

#include "OpenCL/opencl.h"
#include "mason/log.h"

#include <vector>

namespace mason::cl {

    // Has to be called mason_cl_context because otherwise it would conflict with the actual OpenCL type.
    class mason_cl_context {
    public:
        cl_context int_contex;

        mason_cl_context(std::vector<cl_context_properties> props = {}, std::vector<cl_device_id> devs = {},
                         void (CL_CALLBACK *pfn_notify)(const char *errinfo, const void *private_info, size_t cb,
                                                        void *user_data) = nullptr,
                         void *usr_dat = nullptr);

        ~mason_cl_context();

        void bind();
    };
}


#endif //MASONSHARE_MASON_CL_CONTEXT_H
