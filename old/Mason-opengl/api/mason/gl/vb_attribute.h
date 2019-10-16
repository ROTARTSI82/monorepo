//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef MASONSHARE_VB_ATTRIBUTE_H
#define MASONSHARE_VB_ATTRIBUTE_H

#include "mason/gl/gl_core.h"

namespace mason::gl {
    struct vb_attribute {
    public:
        GLint count;
        GLenum type;
        GLboolean normalized;
        size_t pointer;  // The actual type is GLsizei but I choose to use an unsigned type.
    };
}

#endif //MASONSHARE_VB_ATTRIBUTE_H
