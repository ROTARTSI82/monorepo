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

namespace cel {
    class vertex_array {
    private:
        GLuint vao;
    public:
        vertex_array();
        ~vertex_array();
        inline void bind_record() { glBindVertexArray(vao); }

        inline void vertexAttribPointer(GLuint index, GLint size, GLenum type, GLboolean normalized, GLsizei stride, const GLvoid *pointer) {
            glVertexAttribPointer(index, size, type, normalized, stride, pointer);
        }

        inline void enableVertexAttribArray(GLuint index) { glEnableVertexAttribArray(index); }

        inline void set_instanced(GLuint index, bool enable) { glVertexAttribDivisor(index, enable); }

        inline void finalize() {}

        vertex_array(const vertex_array &rhs) = delete;
        vertex_array &operator=(const vertex_array &rhs) = delete;


        inline void bind() { glBindVertexArray(vao); }
    };

    class rectangle_vbo {
    private:
        GLuint vbo;
        rectangle_vbo();
        ~rectangle_vbo();

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

        inline GLuint get_id() { return id; }
    };

    class shader_pipeline {
    private:
        GLuint id;
    public:
        shader_pipeline();
        ~shader_pipeline();

        inline void use() { glUseProgram(id); }
        inline void link() { glLinkProgram(id); }
        inline void attach(GLuint sh) { glAttachShader(id, sh); }
    };

}

#endif