//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef __MASON_VB_ATTRIBUTE_H
#define __MASON_VB_ATTRIBUTE_H

#include "mason/gl/gl_core.h"

namespace mason::gl {
    struct vb_attribute {
    public:
        GLint count;
        GLenum type;
        GLboolean normalized;
        GLsizei pointer;  // The actual type is GLsizei but I choose to use an unsigned type.
    };
}

#endif //__MASON_VB_ATTRIBUTE_H
