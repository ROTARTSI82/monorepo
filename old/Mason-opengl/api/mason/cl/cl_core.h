//
// Created by 25granty on 10/28/19.
//

#pragma once

#ifndef MASONSHARE_CL_CORE_H
#define MASONSHARE_CL_CORE_H

#include "mason/cl/cl_config.h"

#include <vector>

namespace mason::cl {

    template<typename T>
    ::cl::Buffer *buf_from_vec(::cl::CommandQueue *queue, std::vector<T> buf, bool readonly = false);

    template<typename T>
    std::vector<T> read_buf(::cl::CommandQueue *queue, ::cl::Buffer *buf);
}


#endif //MASONSHARE_CL_CORE_H
