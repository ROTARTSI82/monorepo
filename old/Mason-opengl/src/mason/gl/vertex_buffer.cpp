//
// Created by Grant on 9/27/19.
//

#include "mason/gl/vertex_buffer.h"

namespace mason::gl {

    vertex_buffer::vertex_buffer(GLsizeiptr size, const GLvoid *data, GLenum usage) {
        glGenBuffers(1, &id);
        bind();

        glBufferData(GL_ARRAY_BUFFER, size, data, usage);
    }

    vertex_buffer::vertex_buffer(const std::vector<float> &buffer_data) {
        glGenBuffers(1, &id);
        bind();

        glBufferData(GL_ARRAY_BUFFER, buffer_data.size() * sizeof(float), &buffer_data[0], GL_STATIC_DRAW);
    }

    vertex_buffer::~vertex_buffer() {
        glDeleteBuffers(1, &id);
    }

    void vertex_buffer::bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    std::vector<float> pack_vertex_data(const std::vector<vertex_data> &data) {
        std::vector<float> buffer_data;
        for (const vertex_data &dat : data) {
            buffer_data.emplace_back(dat.position.x);
            buffer_data.emplace_back(dat.position.y);
            buffer_data.emplace_back(dat.position.z);

            buffer_data.emplace_back(dat.normal.x);
            buffer_data.emplace_back(dat.normal.y);
            buffer_data.emplace_back(dat.normal.z);

            buffer_data.emplace_back(dat.uv.x);
            buffer_data.emplace_back(dat.uv.y);

            buffer_data.emplace_back(dat.color.r);
            buffer_data.emplace_back(dat.color.g);
            buffer_data.emplace_back(dat.color.b);
            buffer_data.emplace_back(dat.color.a);
        }

        return buffer_data;
    }
}
