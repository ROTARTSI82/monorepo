/**
 * @file gl.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-21
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_GL_HPP
#define CEL_ENG_GL_HPP

#include <GL/glew.h>
#include <string>
#include <unordered_set>
#include <iostream>

#include "cel/eng/misc_util.hpp"

namespace cel {
    class vertex_array {
    private:
        GLuint vao;
    public:
        vertex_array();
        ~vertex_array();
        CEL_DEL_CPY_OP_CTOR(vertex_array)

        inline void bind_record() { glBindVertexArray(vao); }

        inline void vertexAttribPointer(GLuint index, GLint size, GLenum type, GLboolean normalized, GLsizei stride, const GLvoid *pointer) {
            glVertexAttribPointer(index, size, type, normalized, stride, pointer);
        }

        inline void enableVertexAttribArray(GLuint index) { glEnableVertexAttribArray(index); }

        inline void set_instanced(GLuint index, bool enable) { glVertexAttribDivisor(index, enable); }

        inline void finalize() {}

        inline void bind() { glBindVertexArray(vao); }
    };

    class quad_vbo {
    public:
        GLuint value;

        quad_vbo();
        ~quad_vbo();
        CEL_DEL_CPY_OP_CTOR(quad_vbo)
    };

    class shader {
    private:
        GLuint id;
    public:
        /**
         * @brief Construct a new shader object
         * @throws std::runtime_error if shader compilation fails and this shader is marked essential.
         * 
         * @param type Shader type: can be GL_VERTEX_SHADER, GL_FRAGMENT_SHADER, or GL_COMPUTE_SHADER. Refer to glCreateShader()
         * @param source GLSL source code for the shader.
         * @param essential If true, an exception is thrown if the shader can't be compiled.
         */
        shader(GLenum type, const std::string &source, bool essential = false);
        ~shader();
        CEL_DEL_CPY_OP_CTOR(shader)

        inline GLuint get_id() { return id; }
    };

    class shader_pipeline {
    private:
        GLuint id;
    public:
        shader_pipeline();
        ~shader_pipeline();
        CEL_DEL_CPY_OP_CTOR(shader_pipeline)

        inline void use() { glUseProgram(id); }
        inline void link() { glLinkProgram(id); }
        inline void attach(GLuint sh) { glAttachShader(id, sh); }
        inline GLint get_uniform(const GLchar *name) { return glGetUniformLocation(id, name); }
    };

    class texture {
    protected:
        GLuint id;

        inline void generate() {
            glGenTextures(1, &id);
            glBindTexture(GL_TEXTURE_2D, id);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        }

        void load(const std::string &filepath, bool essential, GLenum format) const;

    public:
        texture() = delete;
        texture(GLsizei width, GLsizei height, GLenum format = GL_RGBA);

        /**
         * @brief Construct a new texture object from an image file
         * @throws std::runtime_error if the texture cannot be loaded from the file and is marked essential.
         * @param filepath File to load texture from
         * @param essential If true, an exception is thrown if the texture can't be loaded.
         * @param format The desired internalFormat of the texture (see glTexImage2D)
         */
        texture(const std::string &filepath, bool essential = false, GLenum format = GL_RGBA);
        ~texture();
        CEL_DEL_CPY_OP_CTOR(texture)

        inline void bind() { glBindTexture(GL_TEXTURE_2D, id); }
        inline GLuint get_id() const { return id; }
    };

    class tracked_texture;
}

namespace std {

    template<> 
    struct hash<cel::tracked_texture> {
        std::size_t operator()(const cel::tracked_texture &s) const noexcept;
    };
}

namespace cel {

    class settings_handler;
    class tracked_texture : public texture {
    private:
        GLenum format; // why do i have to store this >:[
        std::unordered_set<tracked_texture> *store;
        std::string name;
        int ref_count = 0;
        bool essential;

    public:

        tracked_texture() = delete;
        tracked_texture(const std::string &fp, std::unordered_set<tracked_texture> *st, settings_handler *opt, bool essential = false, GLenum format = GL_RGBA);

        inline bool operator==(const tracked_texture &rhs) const noexcept {
            return id == rhs.id;
        };

        CEL_DEL_CPY_OP_CTOR(tracked_texture);

        void reload(settings_handler *opt) const;

        inline void inc_ref() {
            ++ref_count;
        }

        inline void dec_ref() {
            if (--ref_count <= 0) { 
                std::cout << "DESTROY TRACKED TEXTURE" << std::endl;
                store->erase(*this); 
            }
        }

        inline int get_ref() const {
            return ref_count;
        }
    };

    class texture_manager {
    private:
        std::unordered_set<tracked_texture> textures;
        settings_handler *opt;

    public:
        texture_manager() = delete;
        texture_manager(settings_handler *opt);

        CEL_DEL_CPY_OP_CTOR(texture_manager);

        counting_ref<tracked_texture> new_tex(const std::string &name, bool essential = false, GLenum format = GL_RGBA);

        void reload_all();
        void cycle_gc();
    };

    typedef counting_ref<tracked_texture> texture_ref;

    class framebuffer {
    private:
        GLuint id, rbo;
        texture tex;

    public:
        GLsizei width, height;

        framebuffer() = delete;

        /**
         * @brief Construct a new framebuffer object
         * @throws std::runtime_error if the framebuffer is incomplete after construction
         * @param width Width of the internal texture/renderbuffer
         * @param height Height of the internal texture/renderbuffer
         * @param format Format for the internal texture
         */
        framebuffer(GLsizei width, GLsizei height, GLenum format = GL_RGBA);
        ~framebuffer();
        CEL_DEL_CPY_OP_CTOR(framebuffer)

        inline void bind() { glBindFramebuffer(GL_FRAMEBUFFER, id); }
        inline void bind_texture() { tex.bind(); }

        /**
         * @brief Resizes the internal texture and renderbuffer
         * @throws std::runtime_error if the framebuffer is incomplete after resize.
         * @param width New width for the texture/renderbuffer
         * @param height New height for the texture/renderbuffer
         * @param format New format for the texture
         */
        void resize(GLsizei width, GLsizei height, GLenum format = GL_RGBA);
    };

    void flush_gl_errors();

}

#endif