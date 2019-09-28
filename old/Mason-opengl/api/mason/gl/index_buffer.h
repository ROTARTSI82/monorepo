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
        GLuint id = 0;

        explicit index_buffer(std::vector<unsigned int> indices);

        index_buffer(GLsizeiptr size, const GLvoid *data, GLenum usage);

        ~index_buffer();

        void bind();
    };
}


#endif //MASONSHARE_INDEX_BUFFER_H
