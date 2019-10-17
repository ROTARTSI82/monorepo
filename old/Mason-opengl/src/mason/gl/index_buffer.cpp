//
// Created by Grant on 9/27/19.
//

#include "mason/gl/index_buffer.h"

namespace mason::gl {

    index_buffer::index_buffer(std::vector<unsigned int> indices) {
        glGenBuffers(1, &id);
        bind();

        count = indices.size();
        type = GL_UNSIGNED_INT;

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(unsigned int) * indices.size(), &indices[0], GL_STATIC_DRAW);
    }

    index_buffer::index_buffer(GLsizeiptr size, const GLvoid *data, GLenum usage, GLenum type, unsigned count) : type(
            type), count(count) {
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

    void index_buffer::draw(GLenum mode) {
        bind();
        glDrawElements(mode, count, type, nullptr);
    }
}
