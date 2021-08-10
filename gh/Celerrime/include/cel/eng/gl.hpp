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

#define CEL_DEL_CPY_OP_CTOR(name) name &operator=(const name &rhs) noexcept = delete; name(const name &rhs) noexcept = delete;

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

    class rectangle_vbo {
    private:
        GLuint vbo;
        rectangle_vbo();
        ~rectangle_vbo();
        CEL_DEL_CPY_OP_CTOR(rectangle_vbo)

        inline static rectangle_vbo *value() {
            static thread_local rectangle_vbo *val = new rectangle_vbo;
            return val;
        }

    public:
        inline static GLuint get() {
            return value()->vbo;
        }

        inline static void destroy() {
            delete value();
        }
    };

    class shader {
    private:
        GLuint id;
    public:
        /**
         * @brief Construct a new shader object
         * @throws std::runtime_error if shader compilation fails.
         * 
         * @param type Shader type: can be GL_VERTEX_SHADER, GL_FRAGMENT_SHADER, or GL_COMPUTE_SHADER. Refer to glCreateShader()
         * @param source GLSL source code for the shader.
         */
        shader(GLenum type, const std::string &source);
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
    private:
        GLuint id;

        inline void generate() {
            glGenTextures(1, &id);
            glBindTexture(GL_TEXTURE_2D, id);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        }

    public:
        texture() = delete;
        texture(GLsizei width, GLsizei height, GLenum format = GL_RGBA);

        /**
         * @brief Construct a new texture object from an image file
         * @throws std::system_error if the texture cannot be loaded from the file. In this case,
         *                           the texture is a 2 px by 2 px placeholder: (red,  green), 
         *                                                                      (blue, white)
         * @param filepath File to load texture from
         * @param format The desired internalFormat of the texture (see glTexImage2D)
         */
        texture(const std::string &filepath, GLenum format = GL_RGBA);
        ~texture();
        CEL_DEL_CPY_OP_CTOR(texture)

        inline void bind() { glBindTexture(GL_TEXTURE_2D, id); }
        inline GLuint get_id() { return id; }
    };

    class framebuffer {
    private:
        GLuint id, rbo;
        texture tex;

    public:
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