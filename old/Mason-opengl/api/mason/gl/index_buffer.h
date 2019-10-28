//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef __MASON_INDEX_BUFFER_H
#define __MASON_INDEX_BUFFER_H

#include "mason/gl/gl_core.h"

#include <vector>

namespace mason::gl {
    class index_buffer {
    public:
        GLuint id = 0;
        GLsizei count = 0;
        GLenum type;

        explicit index_buffer(std::vector<unsigned int> indices);

        index_buffer(GLsizeiptr size, const GLvoid *data, GLenum usage, GLenum type, unsigned count);

        ~index_buffer();

        void bind();

        void draw(GLenum mode = GL_TRIANGLES);
    };
}


#endif //__MASON_INDEX_BUFFER_H
