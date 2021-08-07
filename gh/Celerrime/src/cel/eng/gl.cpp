#include "cel/eng/gl.hpp"
#include "cel/eng/log.hpp"
#include "cel/eng/linalg.hpp"

namespace cel {
    vertex_array::vertex_array() {
        glGenVertexArrays(1, &vao);
    }

    vertex_array::~vertex_array() {
        glDeleteVertexArrays(1, &vao);
    }

    rectangle_vbo::rectangle_vbo() {
        CEL_TRACE("Init rectangle_vbo");

        gl_float data[] = {
            -1, -1,
             1, -1,
             1,  1,
             1,  1,
            -1,  1,
            -1, -1
        };

        glGenBuffers(1, &vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, sizeof(data), data, GL_STATIC_DRAW);
    }

    rectangle_vbo::~rectangle_vbo() {
        CEL_TRACE("Destroy rectangle_vbo");
        glDeleteBuffers(1, &vbo);
    }

    shader_pipeline::shader_pipeline() : id(glCreateProgram()) {}

    shader_pipeline::~shader_pipeline() {
        glDeleteProgram(id);
    }

    shader::shader(GLenum type, const std::string &source) : id(glCreateShader(type)) {
        const char *c_str = source.c_str();
        glShaderSource(id, 1, &c_str, nullptr); // nullptr signals null-terminated strings.
        glCompileShader(id);

        GLint success, len;
        glGetShaderiv(id, GL_COMPILE_STATUS, &success);
        glGetShaderiv(id, GL_INFO_LOG_LENGTH, &len);

        GLchar *msg = new GLchar[len];
        glGetShaderInfoLog(id, len, NULL, msg);
        CEL_INFO("{} shader {}: {}", type, id, msg);
        delete[] msg;

        if (!success) throw std::runtime_error{"Failed to compile shader"};
    }

    shader::~shader() {
        glDeleteShader(id);
    }
}