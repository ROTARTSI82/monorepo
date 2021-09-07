#include "cel/eng/gl.hpp"
#include "cel/eng/log.hpp"
#include "cel/settings.hpp"
#include "cel/eng/linalg.hpp" // REQUIRED! identity_tag is defined here.

#define STB_IMAGE_IMPLEMENTATION
#include "stb/stb_image.h"

#include <errno.h>
#include <cfenv>

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

    void texture::load(const std::string &filepath, bool essential, GLenum format) const {
        int x, y, n;
        unsigned char *data = stbi_load(filepath.c_str(), &x, &y, &n, 0);
        if (!data || !x || !y || !n) { 
            CEL_WARN("Failed to load {}: x={}, y={}, n={}, data={}", filepath, x, y, n, data);
            if (data) stbi_image_free(data);

            unsigned char placeholder[][3] = {{0xff, 0x00, 0x00}, {0x00, 0xff, 0x00},
                                            {0x00, 0x00, 0xff}, {0xff, 0xff, 0xff}};
            glTexImage2D(GL_TEXTURE_2D, 0, format, 2, 2, 0, GL_RGB, GL_UNSIGNED_BYTE, placeholder);

            if (essential) throw std::runtime_error{"Failed to load texture"};
            return;
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

    texture::texture(GLsizei width, GLsizei height, GLenum format) {
        generate();

        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
    }

    texture::texture(const std::string &filepath, bool essential, GLenum format) {
        generate();
        load(filepath, essential, format);
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

        if (errno && errno != 11) {
            CEL_ERROR("ERRNO {}: {}", errno, strerror(errno));
            errno = 0;
        }

        if (std::fetestexcept(FE_DIVBYZERO))
            CEL_ERROR("Floating point divide by 0 reported!");
        // if (std::fetestexcept(FE_INEXACT))
        //     CEL_TRACE("Float inexact result");
        // if (std::fetestexcept(FE_INVALID))
        //     CEL_ERROR("Invalid floating point result (e.g. NaN) reported!");
        if (std::fetestexcept(FE_OVERFLOW))
            CEL_ERROR("Floating point overflow reported (Inf)");
        if (std::fetestexcept(FE_UNDERFLOW)) 
            CEL_ERROR("Floating point underflow reported (-Inf)");
        
        std::feclearexcept(FE_ALL_EXCEPT);

    }


    tracked_texture::tracked_texture(const std::string &fp, std::unordered_set<tracked_texture> *st, settings_handler *opt, bool essential, GLenum format) : 
            texture(opt->res_path(fp), essential, format), store(st), format(format), essential(essential) {}
    
    void tracked_texture::reload(settings_handler *opt) const {
        load(opt->res_path(name), essential, format);
    }

    texture_manager::texture_manager(settings_handler *opt) : opt(opt) {}

    counting_ref<tracked_texture> texture_manager::new_tex(const std::string &name, bool essential, GLenum format) {
        auto res = textures.emplace(name, &textures, opt, essential, format);
        if (!res.second) {
            CEL_WARN("texture_manager::new_tex({}, {}, {}) is returning an existing texture?", name, essential, format);
        }

        return counting_ref<tracked_texture>(&(*res.first));
    }

    void texture_manager::reload_all() {
        for (const auto &i : textures) {
            i.reload(opt);
        }
    }

    void texture_manager::cycle_gc() {
        for (auto it = textures.begin(); it != textures.end();) {
            if (it->get_ref())
                ++it;
            else
                it = textures.erase(it);
        }
    }
}

std::size_t std::hash<cel::tracked_texture>::operator()(const cel::tracked_texture &s) const noexcept {
    return s.get_id();
};
