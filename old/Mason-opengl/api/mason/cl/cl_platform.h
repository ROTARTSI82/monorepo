//
// Created by 25granty on 10/18/19.
//

#pragma once

#ifndef MASONSHARE_CL_PLATFORM_H
#define MASONSHARE_CL_PLATFORM_H

#include "OpenCL/opencl.h"

#include "mason/log.h"

namespace mason::cl {
    class cl_platform {
    public:
        cl_platform_id int_id;

        explicit cl_platform(cl_platform_id id);

        ~cl_platform();

        void bind();
    };

    cl_platform_id *__active_platform = nullptr;

}


#endif //MASONSHARE_CL_PLATFORM_H
