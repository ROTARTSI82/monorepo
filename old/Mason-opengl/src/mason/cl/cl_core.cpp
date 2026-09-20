//
// Created by 25granty on 10/28/19.
//

#include "mason/cl/cl_core.h"

namespace mason::cl {
    template<typename T>
    ::cl::Buffer *buf_from_vec(::cl::CommandQueue *queue, std::vector<T> buf, bool readonly) {
        return new ::cl::Buffer(*queue, buf.begin(), buf.end(), readonly);
    }

    template<typename T>
    std::vector<T> read_buf(::cl::CommandQueue *queue, ::cl::Buffer *buf) {
        size_t size = buf->getInfo<CL_MEM_SIZE>();
        std::vector<T> ret(size / sizeof(T));

        queue->enqueueReadBuffer(*buf, CL_TRUE, 0, size, &ret[0]);
        return ret;
    }

}