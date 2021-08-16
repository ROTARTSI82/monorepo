#include "cel/eng/gl.hpp"
#include "cel/eng/log.hpp"
#include "cel/eng/linalg.hpp" // REQUIRED! identity_tag is defined here.

#define STB_IMAGE_IMPLEMENTATION
#include "stb/stb_image.h"

namespace cel {
    identity_tag_t identity_tag{};

    vertex_array::vertex_array() {
        glGenVertexArrays(1, &vao);
    }

    vertex_array::~vertex_array() {
        CEL_TRACE("~vertex_array()");
        glDeleteVertexArrays(1, &vao);
    }

    quad_vbo::quad_vbo() {
        CEL_TRACE("Init quad_vbo");

        float data[] = {
            -1, -1,
             1, -1,
             1,  1,
             1,  1,
            -1,  1,
            -1, -1
        };

        glGenBuffers(1, &value);
        glBindBuffer(GL_ARRAY_BUFFER, value);
        glBufferData(GL_ARRAY_BUFFER, sizeof(data), data, GL_STATIC_DRAW);
    }

    quad_vbo::~quad_vbo() {
        CEL_TRACE("Destroy quad_vbo");
        glDeleteBuffers(1, &value);
    }

    shader_pipeline::shader_pipeline() : id(glCreateProgram()) {}

    shader_pipeline::~shader_pipeline() {
        glDeleteProgram(id);
    }

    shader::shader(GLenum type, const std::string &source, bool essential) : id(glCreateShader(type)) {
        // CEL_INFO("Shader {} = {}", id, source);

        const char *c_str = source.c_str();
        glShaderSource(id, 1, &c_str, nullptr); // nullptr signals null-terminated strings.
        glCompileShader(id);

        GLint success, len;
        glGetShaderiv(id, GL_COMPILE_STATUS, &success);
        glGetShaderiv(id, GL_INFO_LOG_LENGTH, &len);

        if (len) {
            GLchar *msg = new GLchar[len];
            glGetShaderInfoLog(id, len, NULL, msg);
            CEL_INFO("{} shader {}: {}", type, id, msg);
            delete[] msg;
        }

        if (essential && !success) throw std::runtime_error{"Failed to compile shader"};
    }

    shader::~shader() {
        CEL_TRACE("~shader()");
        glDeleteShader(id);
    }

    texture::texture(GLsizei width, GLsizei height, GLenum format) {
        generate();

        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
    }

    texture::texture(const std::string &filepath, bool essential, GLenum format) {
        generate();
        
        int x, y, n;
        unsigned char *data = stbi_load(filepath.c_str(), &x, &y, &n, 0);
        if (!data || !x || !y || !n) { 
            CEL_WARN("Failed to load {}: x={}, y={}, n={}, data={}", filepath, x, y, n, data);
            if (data) stbi_image_free(data);

            unsigned char placeholder[][3] = {{0xff, 0x00, 0x00}, {0x00, 0xff, 0x00},
                                              {0x00, 0x00, 0xff}, {0xff, 0xff, 0xff}};
            glTexImage2D(GL_TEXTURE_2D, 0, format, 2, 2, 0, GL_RGB, GL_UNSIGNED_BYTE, placeholder);

            if (essential) throw std::runtime_error{"Failed to load texture"};
        }

        GLenum pix_fmt;
        switch (n) {
        case 1:
            pix_fmt = GL_LUMINANCE; break;
        case 2:
            pix_fmt = GL_LUMINANCE_ALPHA; break;
        case 3:
            pix_fmt = GL_RGB; break;
        default:
            pix_fmt = GL_RGBA; break;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, format, x, y, 0, pix_fmt, GL_UNSIGNED_BYTE, data);
        stbi_image_free(data);
    }

    texture::~texture() {
        CEL_TRACE("~texture()");
        glDeleteTextures(1, &id);
    }

    framebuffer::framebuffer(GLsizei width, GLsizei height, GLenum format) : tex{width, height, format}, width(width), height(height) {
        glGenFramebuffers(1, &id);
        glBindFramebuffer(GL_FRAMEBUFFER, id);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex.get_id(), 0);

        glGenRenderbuffers(1, &rbo);
        glBindRenderbuffer(GL_RENDERBUFFER, rbo); 
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status  != GL_FRAMEBUFFER_COMPLETE) {
            CEL_WARN("Failed to construct {}x{} framebuffer: {}", width, height, status);
            throw std::runtime_error{"Framebuffer construction failed"};
        }
    }

    framebuffer::~framebuffer() {
        CEL_TRACE("~framebuffer()");
        glDeleteRenderbuffers(1, &rbo);
        glDeleteFramebuffers(1, &id);
    }

    void framebuffer::resize(GLsizei iwidth, GLsizei iheight, GLenum format) {
        width = iwidth;
        height = iheight;

        glBindFramebuffer(GL_FRAMEBUFFER, id);
        tex.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);

        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);

        GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status  != GL_FRAMEBUFFER_COMPLETE) {
            CEL_WARN("Failed to resize {}x{} framebuffer: {}", width, height, status);
            throw std::runtime_error{"Framebuffer resize failed"};
        }
    }

    void flush_gl_errors() {
        GLenum err;
        while((err = glGetError()) != GL_NO_ERROR) {
            CEL_ERROR("OpenGL Error {}", err);
        }
    }
}