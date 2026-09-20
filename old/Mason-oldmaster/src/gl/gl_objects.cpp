//
// Created by Grant on 2019-08-31.
//

#include "mason/gl/gl_objects.h"

namespace mason::gl {
    /*
 * NOTE: The data type of IndexBuffers is hardcoded to be GLuint (or unsigned int). VertexBuffers can have varying types.
 */
    IndexBuffer::IndexBuffer(const GLuint *data, GLenum usage, GLsizei numIndx, GLenum type) : id(0) {
        glGenBuffers(1, &id);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, VBLayout::typeSizes[type] * numIndx, data, usage);
        this->count = numIndx;
        this->dataType = type;
    }

    void IndexBuffer::bind() const {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    VertexBuffer::VertexBuffer(const GLvoid *data, GLenum usage, GLsizei size) : id(0) {
        glGenBuffers(1, &id);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, size, data, usage);
    }

    void VertexBuffer::bind() const {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    VertexArray::VertexArray() : id(0) {
        glGenVertexArrays(1, &id);
        glBindVertexArray(id);
    }

    void VertexArray::bind() const {
        glBindVertexArray(id);
    }

    void VBLayout::addAttribute(GLint count, GLenum type, GLboolean normalized) {
        attribs.push_back({count, type, normalized, stride});
        stride += typeSizes[type] * count;
    }

    void VertexBuffer::destroy() {
        glDeleteBuffers(1, &id);
    }

    void IndexBuffer::destroy() {
        glDeleteBuffers(1, &id);
    }

    void VertexArray::destroy() {
        glDeleteVertexArrays(1, &id);
    }

    void VertexBuffer::setLayout(VBLayout *lo) {
        layout = lo;
        reloadLayout();
    }

    void VertexBuffer::reloadLayout() {
        bind();

        for (int i = 0; i < layout->attribs.size(); i++) {
            VBAttribute attrib = layout->attribs.at(i);
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attrib.count, attrib.type, attrib.normalized, layout->stride,
                                  (const void *) attrib.pointer);
        }
    }
}