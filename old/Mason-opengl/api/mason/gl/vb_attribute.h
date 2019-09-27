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
        GLsizei pointer;
    };
}

#endif //MASONSHARE_VB_ATTRIBUTE_H
