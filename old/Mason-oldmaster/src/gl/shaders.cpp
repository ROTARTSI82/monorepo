//
// Created by Grant on 2019-08-31.
//

#include <mason/gl/shaders.h>

namespace mason::gl {
    ShaderProgram::ShaderProgram(const std::string &path) {
        std::string profilePath = path + "/program.meta";
        std::ifstream metadat(profilePath);

        std::string line;
        id = glCreateProgram();
        std::vector<GLuint> shaders;

        if (!metadat.is_open()) {
            MASON_WARN("Failed to load shader program: cannot read {}", profilePath);
            id = 0;
            return;
        }

        while (getline(metadat, line)) {
            int indx = line.find(": ");
            if (line[0] == '#' ||
                indx == std::string::npos) { // comments. Skip lines that don't have colons or contain the # character.
                continue;
            }
            std::string shaderStrType = line.substr(0, indx);
            std::string filename = line.substr(indx + 2);
            std::string fullPath = path + "/" + filename;

            std::string shaderSrc;
            std::string shaderln;

            std::ifstream shaderStream(fullPath);

            if (!shaderStream.is_open()) {
                MASON_INFO("Failed to load {} as {}. Skipping this shader!", fullPath, shaderStrType)
                continue;
            }
            while (getline(shaderStream, shaderln)) {
                shaderSrc.append(shaderln + "\n");
            }

            // Shader compilation code.
            GLuint shader = compileShader(shaderStrType, shaderSrc, fullPath);
            if (shader != 0) {
                glAttachShader(id, shader);
                shaders.push_back(shader);
                MASON_INFO("Successfully loaded {} as {}", fullPath, shaderStrType);
            }
        }
        glLinkProgram(id);
        glValidateProgram(id);

        for (GLuint s : shaders) {
            glDeleteShader(s);
        }

        MASON_INFO("Successfully loaded shader program from {}", profilePath)
    }


    GLuint ShaderProgram::compileShader(const std::string &type, const std::string &src, const std::string &fullpath) {
        GLenum shadertype;
        if (shaderTypes.find(type) != shaderTypes.end()) {
            shadertype = shaderTypes[type];
        } else {
            MASON_WARN("Invalid shader type of {} for {}! Skipping this shader!", type, fullpath)
            return 0;
        }

        GLuint shader = glCreateShader(shadertype);
        const char *rsrc = src.c_str();

        glShaderSource(shader, 1, &rsrc, nullptr);
        glCompileShader(shader);

        GLint result;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &result);
        if (result == GL_FALSE) {
            GLint len;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &len);
            auto *msg = new GLchar[len];
            glGetShaderInfoLog(shader, len, &len, msg);
            MASON_WARN("Error compiling {} as {}: \n\n{}", fullpath, type, msg)
            delete[] msg;
            glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    void ShaderProgram::bind() const {
        glUseProgram(id);
    }

    void ShaderProgram::unbind() const {
        glUseProgram(0);
    }

    GLint ShaderProgram::getUniformLoc(const std::string &name) {
        auto loc = uniforms.find(name);
        if (loc != uniforms.end()) {  // It's already cached. No need to access GPU again.
            return loc->second;
        }

        GLint uni = glGetUniformLocation(id, name.c_str());
        if (uni == -1) {
            MASON_WARN("Tried to access uniform {} which doesn't exist!", name)
            return -1;
        }
        uniforms[name] = uni;
        return uni;
    }

    void ShaderProgram::setUniform4f(const std::string &name, float f0, float f1, float f2, float f3) {
        GLint loc = getUniformLoc(name);
        if (loc != -1) {
            glUniform4f(loc, f0, f1, f2, f3);
        }
    }

    void ShaderProgram::setUniform1i(const std::string &name, int v) {
        GLint loc = getUniformLoc(name);
        if (loc != -1) {
            glUniform1i(loc, v);
        }
    }

    void ShaderProgram::setUniformMat4f(const std::string &name, glm::mat4 &mat4, GLboolean transpose) {
        GLint loc = getUniformLoc(name);
        if (loc != -1) {
            glUniformMatrix4fv(loc, 1, transpose, &mat4[0][0]);
        }
    }

    ShaderProgram::~ShaderProgram() {
        glDeleteProgram(id);
    }
}

