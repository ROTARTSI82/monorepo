//
// Created by Grant on 9/27/19.
//

#include "mason/gl/index_buffer.h"

namespace mason::gl {

    index_buffer::index_buffer(const std::vector<unsigned int> indices) {
        glGenBuffers(1, &id);
        bind();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(unsigned int) * indices.size(), &indices[0], GL_STATIC_DRAW);
    }

    index_buffer::index_buffer(GLsizeiptr size, const GLvoid *data, GLenum usage) {
        glGenBuffers(1, &id);
        bind();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, data, usage);
    }

    index_buffer::~index_buffer() {
        glDeleteBuffers(1, &id);
    }

    void index_buffer::bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }
}
