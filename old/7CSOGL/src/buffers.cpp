//
// Created by grant on 5/8/20.
//

#include <iostream>
#include <vector>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#define STB_IMAGE_IMPLEMENTATION

#include <stb/stb_image.h>
#include <fstream>
#include "glm/gtx/euler_angles.hpp"

void flushErrors(const std::string &msg) {
    if (msg != "null") {
        std::cerr << msg << std::endl;
    }

    GLenum err = 1;
    while (err != GL_NO_ERROR) {
        if (err != 1) {
            std::cerr << "OpenGL Error: " << err << std::endl;
        }
        err = glGetError();
    }
}

template<typename T, GLenum type>
class GenericBuffer {
protected:
    GLuint id{};
public:
    GenericBuffer() = default;

    explicit GenericBuffer(std::vector<T> &contents) {
        glGenBuffers(1, &id);
        bind();
        glBufferData(type, contents.size() * sizeof(T), contents.data(), GL_STATIC_DRAW);
    }

    inline void bind() const {
        glBindBuffer(type, id);
    }

    GenericBuffer &operator=(const GenericBuffer &rhs) = delete;
    GenericBuffer(const GenericBuffer &rhs) = delete;

    GenericBuffer &operator=(GenericBuffer &&rhs) noexcept {
        if (&rhs == this) {
            return *this;
        }
        id = rhs.id;
        rhs.id = 0;
        return *this;
    }

    GenericBuffer(GenericBuffer &&rhs) noexcept {
        *this = std::move(rhs);
    }

    virtual ~GenericBuffer() {
        glDeleteBuffers(1, &id);
    }
};

typedef GenericBuffer<float, GL_ARRAY_BUFFER> VBO;

class IBO : public GenericBuffer<unsigned, GL_ELEMENT_ARRAY_BUFFER> {
private:
    GLsizei count{};
public:
    explicit IBO(std::vector<unsigned> &contents) : GenericBuffer<unsigned, GL_ELEMENT_ARRAY_BUFFER>(contents),
            count(contents.size()) {};

    inline void draw() {
        bind();
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, nullptr);
    }

    IBO &operator=(const IBO &rhs) = delete;
    IBO(const IBO &rhs) = delete;

    IBO &operator=(IBO &&rhs) noexcept {
        if (&rhs == this) {
            return *this;
        }
        id = rhs.id;
        count = rhs.count;

        rhs.id = 0;
        return *this;
    }

    IBO(IBO &&rhs) noexcept {
        *this = std::move(rhs);
    }
};

class VAO {
protected:
    GLuint id{};
    std::vector<unsigned> attribs;
    GLsizei stride{};

public:
    VAO() {
        glGenVertexArrays(1, &id);
    }

    inline void bind() const {
        glBindVertexArray(id);
    }

    void pushFloat(unsigned qty) {
        attribs.emplace_back(qty);
        stride += sizeof(float) * qty;
    }

    void finalize() {
        bind();
        GLsizei ptr{};
        for (GLuint i = 0; i < attribs.size(); i++) {
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attribs[i], GL_FLOAT, GL_FALSE, stride, reinterpret_cast<void *>(ptr));
            ptr += attribs[i] * sizeof(float);
        }
    }

    VAO &operator=(const VAO &rhs) = delete;
    VAO(const VAO &rhs) = delete;

    VAO &operator=(VAO &&rhs) noexcept {
        if (&rhs == this) {
            return *this;
        }
        attribs = std::move(rhs.attribs);
        stride = rhs.stride;
        id = rhs.id;

        rhs.id = 0;
        return *this;
    }

    VAO(VAO &&rhs) noexcept {
        *this = std::move(rhs);
    }

    virtual ~VAO() {
        glDeleteVertexArrays(1, &id);
    }
};


class Shader {
private:
    std::string contents;
    GLuint id{};
    bool isVert{};

    friend class ShaderProgram;
public:
    Shader() = default;

    explicit Shader(const std::string &filename, bool isVert) : isVert(isVert) {
        std::ifstream fp;
        fp.open(filename, std::ofstream::in);
        if (!fp.is_open()) {
            throw std::runtime_error("Failed to read " + filename);
        }

        std::string line;
        while (std::getline(fp, line)) {
            contents += line + "\n";
        }
        fp.close();

        id = glCreateShader(isVert ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);

        auto srcCStr = contents.c_str();
        glShaderSource(id, 1, &srcCStr, nullptr);
        glCompileShader(id);

        int  success;
        glGetShaderiv(id, GL_COMPILE_STATUS, &success);

        if (!success) {
            char infoLog[512];
            glGetShaderInfoLog(id, 512, nullptr, infoLog);
            throw std::runtime_error("Failed to compile shader: " + std::string(infoLog));
        }
    }

    ~Shader() {
        glDeleteShader(id);
    }

    Shader &operator=(const Shader &rhs) = delete;
    Shader(const Shader &rhs) = delete;

    Shader &operator=(Shader &&rhs) noexcept {
        if (&rhs == this) {
            return *this;
        }
        id = rhs.id;

        rhs.id = 0;
        return *this;
    }

    Shader(Shader &&rhs) noexcept {
        *this = std::move(rhs);
    }
};

typedef int UniformLocation;

class ShaderProgram {
private:
    GLuint id{};

public:
    ShaderProgram() {
        id = glCreateProgram();
    }

    inline void attach(Shader &shader) const {
        glAttachShader(id, shader.id);
    }

    inline void link() const {
        glLinkProgram(id);
    }

    inline void bind() const {
        glUseProgram(id);
    }

    ~ShaderProgram() {
        glDeleteProgram(id);
    }

    [[nodiscard]] inline UniformLocation getLocation(const std::string &in) const {
        return glGetUniformLocation(id, in.c_str());
    }

    static inline void setMat4(UniformLocation in, glm::mat4 val) {
        glUniformMatrix4fv(in, 1, GL_FALSE, glm::value_ptr(val));
    }

    static inline void set1i(UniformLocation in, GLint val) {
        glUniform1i(in, val);
    }

    ShaderProgram &operator=(const ShaderProgram &rhs) = delete;

    ShaderProgram(const ShaderProgram &rhs) = delete;

    ShaderProgram &operator=(ShaderProgram &&rhs) noexcept {
        if (&rhs == this) {
            return *this;
        }
        id = rhs.id;
        rhs.id = 0;
        return *this;
    }

    ShaderProgram(ShaderProgram &&rhs) noexcept {
        *this = std::move(rhs);
    }
};


class Camera {
private:

    glm::mat4 view;
    glm::mat4 proj;

public:
    glm::vec3 pos;
    glm::vec3 euler;

    glm::vec3 forward;
    glm::vec3 right;
    glm::vec3 up;

    Camera() = default;

    inline void setOri(glm::vec3 eulerCoords) {
        euler = eulerCoords;
    }

    inline void setPos(glm::vec3 newPos) {
        this->pos = newPos;
    }

    inline void updateViewMat() {
        view = glm::eulerAngleXYZ(euler.x, euler.y, euler.z);
        forward = glm::vec4({0, 0, 1, 0}) * view;
        right = glm::vec4({1, 0, 0, 0}) * view;
        up = glm::vec4({0, 1, 0, 0}) * view;
        view = view * glm::translate(glm::mat4(1.0f), pos);
    }

    inline glm::mat4 getView() {
        return view;
    }

    inline glm::mat4 getProj() {
        return proj;
    }

    inline void setProj(float fov, float ratio, float zNear = 0.1f, float zFar = 100.0f) {
        proj = glm::perspective(glm::radians(fov), ratio, zNear, zFar);
    }
};


class Texture {
private:
    GLuint id{};
public:
    Texture() = default;

    explicit Texture(const std::string &file, bool antiAlias = false) {
        int width, height, nrChannels;
        unsigned char *data = stbi_load(file.c_str(), &width, &height, &nrChannels, 0);

        glGenTextures(1, &id);
        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, antiAlias ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, antiAlias ? GL_LINEAR : GL_NEAREST);

        if (data) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            throw std::runtime_error("Failed to load texture: " + file);
        }

    }

    inline void bind() const {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    ~Texture() {
        glDeleteTextures(1, &id);
    }
};
