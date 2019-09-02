//
// Created by Grant on 2019-08-31.
//

#pragma once

#ifndef MASON_TEXTURE_H
#define MASON_TEXTURE_H

#include "mason/masonpch.h"
#include "mason/log.h"

#include "mason/gl/gl_includes.h"



namespace mason::gl {
    class Image {
    public:
        std::string fp;
        int width = 0, height = 0, channels = 0;
        stbi_uc *data;

        GLenum desiredFmt = 0;
        GLint dataFmt = 0;

        explicit Image(const std::string &path, bool flip = false);

        void free() {
            stbi_image_free(data);
        };

        ~Image() {
            free();
        }
    };

    class Texture2D {
    public:
        GLuint id = 0;
        int width, height, bits;

        explicit Texture2D(Image *img, GLint lod = 0, GLint border = 0);

        ~Texture2D();

        void genMipmaps();

        static void setHints(GLenum textureType, const std::unordered_map<GLenum, GLenum> &hints);

        void bind(GLenum slot = GL_TEXTURE0);

        void addSubImage(Image *img, GLint x = 0, GLint y = 0, GLsizei w = -1, GLsizei h = -1, GLint level = 0);
    };
}
#endif //MASON_TEXTURE_H
