//
// Created by Grant on 9/27/19.
//

#define STB_IMAGE_IMPLEMENTATION

#include "mason/gl/texture2d.h"

namespace mason::gl {

    void texture2d::add_subimage(mason::image *img, GLint x, GLint y, GLsizei w, GLsizei h, GLint level) {
        if (!w) (w = img->width);
        if (!h) (h = img->height);

        bind();
        glTexSubImage2D(GL_TEXTURE_2D, level, x, y, w, h, img->channels > 3 ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE,
                        img->data);
    }

    void texture2d::bind_slot(GLenum slot) {
        glActiveTexture(slot);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    void texture2d::bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    texture2d::texture2d(mason::image *img, GLint lod, GLint border) {
        glGenTextures(1, &id);
        bind();

        glTexImage2D(GL_TEXTURE_2D, lod, GL_RGBA8, img->width, img->height, border, GL_RGBA, GL_UNSIGNED_BYTE,
                     img->data);

        set_texture_hints(); // Hints need to be set or the texture would simply render a blank
    }

    texture2d::~texture2d() {
        glDeleteTextures(1, &id);
    }

    void texture2d::generate_mipmaps() {
        bind();
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    void set_texture_hints(GLenum texture_type, const std::unordered_map<GLenum, GLenum> &hints) {
        for (auto pair : hints) {
            glTexParameteri(texture_type, pair.first, pair.second);
        }
    }

    void set_active_texture_slot(GLenum slot) {
        glActiveTexture(slot);
    }
}
