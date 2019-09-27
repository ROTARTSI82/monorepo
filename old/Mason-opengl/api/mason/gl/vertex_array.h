//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef MASONSHARE_VERTEX_ARRAY_H
#define MASONSHARE_VERTEX_ARRAY_H

#include "mason/gl/gl_core.h"

namespace mason::gl {
    /*
     * The following snippet would initiate the VAO
     *
     * vert_array.bind();
     * ibo.bind();
     * vbo.bind();
     * layout.set_attributes();
     *
     * So instead of writing the following before every render:
     *
     * ibo.bind(); vbo.bind(); layout.set_attributes();
     *
     * You could just run `vert_array.bind();`
     */
    class vertex_array {
    public:
        unsigned int id = 0;

        vertex_array();

        ~vertex_array();

        void bind();
    };
}
#endif //MASONSHARE_VERTEX_ARRAY_H
