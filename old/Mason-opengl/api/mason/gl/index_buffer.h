//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef MASONSHARE_INDEX_BUFFER_H
#define MASONSHARE_INDEX_BUFFER_H

#include "mason/gl/gl_core.h"

#include <vector>

namespace mason::gl {
    class index_buffer {
    public:
        unsigned int id = 0;

        index_buffer(const std::vector<unsigned int> indices);

        index_buffer(GLsizeiptr size, const GLvoid *data, GLenum usage);

        ~index_buffer();

        void bind();
    };
}


#endif //MASONSHARE_INDEX_BUFFER_H
