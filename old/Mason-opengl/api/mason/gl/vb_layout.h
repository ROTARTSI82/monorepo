//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef __MASON_VB_LAYOUT_H
#define __MASON_VB_LAYOUT_H

#include "mason/gl/gl_core.h"
#include "mason/gl/vb_attribute.h"

namespace mason::gl {
    class vb_layout {
    public:
        std::vector<vb_attribute> attributes;
        GLsizei stride = 0;

        vb_layout() = default;

        virtual ~vb_layout() = default;

        template<typename T>
        void push_attribute(GLenum type, int quantity, GLboolean normalize = GL_FALSE);

        void set_attributes();

        void disable_attributes();
    };

    vb_layout *default_layout = nullptr;

    vb_layout *get_default_layout();
}

#endif //__MASON_VB_LAYOUT_H
