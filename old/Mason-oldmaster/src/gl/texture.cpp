//
// Created by Grant on 2019-08-31.
//
#define STB_IMAGE_IMPLEMENTATION

#include "mason/gl/texture.h"

namespace mason::gl {
    Image::Image(const std::string &path, bool flip) : fp(path) {
        if (flip) {
            stbi_set_flip_vertically_on_load(1);
        } else {
            stbi_set_flip_vertically_on_load(0);
        }

        data = stbi_load(path.c_str(), &width, &height, &channels, 4); // 4 channels for RGBA

        mason::logAssert(data, fmt::format("Failed to load image at {}", path));

        dataFmt = GL_RGBA8;
        desiredFmt = GL_RGBA;

        mason::logAssert(dataFmt & desiredFmt, fmt::format("Cannot process {}-bit image at {}", channels, fp));
    }

    Texture2D::Texture2D(int w, int h) : width(w), height(h), bits(4) {
        width = w;
        height = h;

        glGenTextures(1, &id);
        clear();
        // Need to set the hints, or else we get a black texture
        setHints(GL_TEXTURE_2D, {{GL_TEXTURE_MIN_FILTER, GL_NEAREST},
                                 {GL_TEXTURE_MAG_FILTER, GL_NEAREST}});
    }

    Texture2D::Texture2D(Image *img, GLint lod, GLint border) : bits(img->channels), width(img->width),
                                                                height(img->height) {
        glGenTextures(1, &id);
        bind();

        // DEFAULT VALUES
        // {GL_TEXTURE_WARP_S, GL_CLAMP}, {GL_TEXTURE_WARP_T, GL_CLAMP}}); // These for some reason don't work :(

        glTexImage2D(GL_TEXTURE_2D, lod, img->dataFmt, img->width, img->height, border, img->desiredFmt,
                     GL_UNSIGNED_BYTE, img->data);

        // Need to set the hints, or else we get a black texture
        setHints(GL_TEXTURE_2D, {{GL_TEXTURE_MIN_FILTER, GL_NEAREST},
                                 {GL_TEXTURE_MAG_FILTER, GL_NEAREST}});
    }

    void Texture2D::clear() {
        bind();
        auto emptyData = new std::vector<GLubyte>(width * height * 4, 0);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, &emptyData[0]);
        delete emptyData;
    }

    void Texture2D::addSubImage(Image *img, GLint x, GLint y, GLsizei w, GLsizei h, GLint level) {

        if (w == -1) {
            w = img->width;
        }
        if (h == -1) {
            h = img->height;
        }

        bind();
        glTexSubImage2D(GL_TEXTURE_2D, level, x, y, w, h, img->desiredFmt, GL_UNSIGNED_BYTE, img->data);

    }

    void Texture2D::bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    void Texture2D::gl4bind(GLuint unit) {
        glBindTextureUnit(unit, id);
    }

    void Texture2D::setHints(GLenum textureType, const std::unordered_map<GLenum, GLenum> &hints) {
        for (std::pair<GLenum, GLint> pair : hints) {
            glTexParameteri(textureType, pair.first, pair.second);
        }
    }

    Texture2D::~Texture2D() {
        glDeleteTextures(1, &id);
    }

    void Texture2D::genMipmaps() {
        bind();
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    void Texture2D::activateSlot(GLenum slot) {
        glActiveTexture(slot);
    }
}