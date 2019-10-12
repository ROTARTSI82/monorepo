//
// Created by Grant on 9/27/19.
//

#include "mason/gl/shader_program.h"

namespace mason::gl {

    shader_program::shader_program(const std::string &path) {
        std::string meta_path = path + "/metadata.txt";
        std::ifstream meta_stream(meta_path);

        std::string line;
        std::vector<GLuint> shaders;

        if (!meta_stream.is_open()) {
            MASON_WARN("Failed to load shader program: cannot read {}", meta_path);
            id = 0;
            return;
        } else {
            id = glCreateProgram();
        }

        while (getline(meta_stream, line)) {
            // Ignore whitespace and comments
            if (line.empty() || line[0] == '#') {
                return;
            }

            std::size_t pos = line.find(' ');
            while (pos != std::string::npos) {
                line.replace(pos, 1, "");
                pos = line.find(' ');
            }

            pos = line.find('\t');
            while (pos != std::string::npos) {
                line.replace(pos, 1, "");
                pos = line.find('\t');
            }

            if (line.empty() || line[0] == '#') {
                continue;
            }

            // Process line.

            unsigned long index = line.find(':');

            std::string shader_type_str = line.substr(0, index);
            std::string filename = line.substr(index + 1);

            std::string full_path = path;
            full_path += "/" + filename;


            std::string shader_source;
            std::string current_line;

            std::ifstream shader_stream(full_path);

            if (!shader_stream.is_open()) {
                MASON_INFO("Failed to load {} as {}. Skipping this shader!", full_path, shader_type_str);
                continue;
            }
            while (getline(shader_stream, current_line)) {
                shader_source.append(current_line + "\n");
            }

            // Shader compilation code.
            GLuint shader = compile_shader(shader_type_str, shader_source, full_path);
            if (shader != 0) {
                glAttachShader(id, shader);
                shaders.push_back(shader);
                MASON_INFO("Successfully loaded {} as {}", full_path, shader_type_str);
            }  // We don't need to handle logging here, as it is already handled in compile_shader.
        }
        glLinkProgram(id);
        glValidateProgram(id);

        for (GLuint s : shaders) {
            glDeleteShader(s);
        }

        MASON_INFO("Successfully loaded {} shaders from {}", shaders.size(), meta_path);
    }

    shader_program::~shader_program() {
        glDeleteProgram(id);
    }

    void shader_program::bind() {
        glUseProgram(id);
    }

    GLint shader_program::get_uniform_location(const std::string &name) {
        auto loc = uniform_cache.find(name);
        if (loc != uniform_cache.end()) {
            return loc->second;
        }

        GLint uni = glGetUniformLocation(id, name.c_str());
        if (uni == -1) {
            MASON_WARN("Tried to access uniform {} which doesn't exist!", name);
            return -1;
        }
        uniform_cache[name] = uni;
        return uni;
    }

    void shader_program::set_uniform_3f(const std::string &name, float f1, float f2, float f3) {
        GLint loc = get_uniform_location(name);
        if (loc != -1) {
            glUniform3f(loc, f1, f2, f3);
        }
    }

    void shader_program::set_uniform_mat4fv(const std::string &name, glm::mat4 &mat, GLboolean transpose) {
        GLint loc = get_uniform_location(name);
        if (loc != -1) {
            glUniformMatrix4fv(loc, 1, transpose, &mat[0][0]);
        }
    }

    void shader_program::set_uniform_1i(const std::string &name, int i) {
        GLint loc = get_uniform_location(name);
        if (loc != -1) {
            glUniform1i(loc, i);
        }
    }

    GLuint compile_shader(const std::string &type, const std::string &src, const std::string &full_path) {
        GLenum type_enum;

        if (type == "vertex-shader") {
            type_enum = GL_VERTEX_SHADER;
        } else if (type == "fragment-shader") {
            type_enum = GL_FRAGMENT_SHADER;
        } else {
            MASON_WARN("Received invalid shader type {} for {}! Skipping!", type, full_path);
            return 0;
        }

        GLuint shader = glCreateShader(type_enum);
        const char *raw_src = src.c_str();

        glShaderSource(shader, 1, &raw_src, nullptr);
        glCompileShader(shader);

        GLint result;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &result);
        if (result == GL_FALSE) {
            GLint len;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &len);
            auto *msg = new GLchar[len];
            glGetShaderInfoLog(shader, len, &len, msg);
            MASON_WARN("Error compiling {} as {}: \n\n{}", full_path, type, msg);
            delete[] msg;
            glDeleteShader(shader);
            return 0;
        }

        return shader;
    }
}
