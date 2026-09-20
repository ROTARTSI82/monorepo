//
// Created by Grant on 9/27/19.
//

#include "mason/gl/vertex_array.h"

namespace mason::gl {

    vertex_array::vertex_array() {
        glGenVertexArrays(1, &id);
    }

    vertex_array::~vertex_array() {
        glDeleteVertexArrays(1, &id);
    }

    void vertex_array::bind() {
        glBindVertexArray(id);
    }
}
