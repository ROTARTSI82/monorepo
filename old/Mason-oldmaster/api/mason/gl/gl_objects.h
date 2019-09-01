//
// Created by Grant on 2019-08-31.
//

#ifndef MASON_GL_OBJECTS_H
#define MASON_GL_OBJECTS_H

#include "mason/masonpch.h"

#include "GL/glew.h"
#include "GLFW/glfw3.h"

namespace mason::gl {
    struct VBAttribute {
    public:
        GLint count;
        GLenum type;
        GLboolean normalized;
        GLsizei pointer;
    };

    class IndexBuffer {
    public:
        GLuint id;
        GLenum dataType;
        GLsizei count;

        IndexBuffer(const GLuint *data, GLenum usage, GLsizei numIndx, GLenum type);

        ~IndexBuffer() { destroy(); }

        void bind() const;

        void destroy();
    };

    class VBLayout {
    public:
        inline static std::unordered_map<GLenum, GLsizei> typeSizes = {
                {GL_FLOAT,        sizeof(GLfloat)},
                {GL_INT,          sizeof(GLint)},
                {GL_UNSIGNED_INT, sizeof(GLuint)}
        };
        std::vector<VBAttribute> attribs;
        GLsizei stride = 0;

        VBLayout() = default;

        void addAttribute(GLint count, GLenum type, GLboolean normalized);
    };

    class VertexArray {
    public:
        GLuint id;

        VertexArray();

        ~VertexArray() { destroy(); }

        void bind() const;

        void destroy();
    };

    class VertexBuffer {
    public:
        VBLayout *layout = nullptr;
        GLuint id;

        VertexBuffer(const GLvoid *data, GLenum usage, GLsizei size);

        ~VertexBuffer() { destroy(); }

        void bind() const;

        void setLayout(VBLayout *layout);

        void reloadLayout();

        void destroy();
    };

}
#endif //MASON_GL_OBJECTS_H
