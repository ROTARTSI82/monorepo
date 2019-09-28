//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef MASONSHARE_TEXTURE2D_H
#define MASONSHARE_TEXTURE2D_H

#include "mason/gl/gl_core.h"
#include "mason/image.h"

#include <unordered_map>

namespace mason::gl {
    class texture2d {
        GLuint id = 0;

        explicit texture2d(mason::image *img, GLint lod = 0, GLint border = 0);

        ~texture2d();

        void bind(GLenum slot = GL_TEXTURE0);

        void add_subimage(mason::image *img, GLint x = 0, GLint y = 0, GLsizei w = 0, GLsizei h = 0, GLint level = 0);
    };

    void set_texture_hints(GLenum texture_type, const std::unordered_map<GLenum, GLenum> &hints);
}


#endif //MASONSHARE_TEXTURE2D_H
