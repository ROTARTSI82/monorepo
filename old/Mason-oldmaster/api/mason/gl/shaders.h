//
// Created by Grant on 2019-08-31.
//

#ifndef MASON_SHADERS_H
#define MASON_SHADERS_H

#include "mason/gl/gl_includes.h"

namespace mason::gl {
    class ShaderProgram {
    public:

        inline static std::unordered_map<std::string, GLenum> shaderTypes = {
                {"fragment-shader", GL_FRAGMENT_SHADER},
                {"vertex-shader",   GL_VERTEX_SHADER},
                {"geometry-shader", GL_GEOMETRY_SHADER} // Geometry shaders unsupported in opengl 2.1
        };

        std::unordered_map<std::string, GLint> uniforms = {};
        GLuint id;

        explicit ShaderProgram(const std::string &path);

        ~ShaderProgram();

        void bind() const;

        void unbind() const;

        GLint getUniformLoc(const std::string &name);

        void setUniform1i(const std::string &name, int v);

        void setUniform4f(const std::string &name, float f0, float f1, float f2, float f3);

        void setUniformMat4f(const std::string &name, glm::mat4 &mat4, GLboolean transpose = GL_FALSE);

        GLuint compileShader(const std::string &type, const std::string &src, const std::string &fullpath);
    };
}
#endif //MASON_SHADERS_H
