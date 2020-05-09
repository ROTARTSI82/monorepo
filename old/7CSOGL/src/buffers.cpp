//
// Created by grant on 5/8/20.
//

#include <iostream>
#include <vector>
#include <GL/glew.h>
#include <fstream>
#include <GLFW/glfw3.h>

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
