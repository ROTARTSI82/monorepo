//
// Created by 25granty on 10/19/19.
//

#pragma once

#ifndef MASONSHARE_CL_PROGRAM_H
#define MASONSHARE_CL_PROGRAM_H

#include "OpenCL/opencl.h"
#include <string>

#include "mason/log.h"

namespace mason::cl {
    class __mason_cl_program {  // Again, it would conflict with the actual OpenCL type otherwise
    public:
        cl_program int_prog;

        __mason_cl_program(const std::string &path);

        ~__mason_cl_program();

        void bind();
    };

    cl_program *__active_program = nullptr;

    typedef __mason_cl_program cl_program;
}


#endif //MASONSHARE_CL_PROGRAM_H
