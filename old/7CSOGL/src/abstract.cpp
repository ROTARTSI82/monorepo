//
// Created by grant on 5/8/20.
//

#include <iostream>
#include <vector>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include <al.h>
#include <alc.h>

#define STB_IMAGE_IMPLEMENTATION

#include <stb/stb_image.h>
#include <stb/stb_vorbis.c>
#include <fstream>
#include "glm/gtx/euler_angles.hpp"

void flushErrors(const std::string &msg) {

    GLenum err = 1;
    while (err != GL_NO_ERROR) {
        if (err != 1) {
            std::cerr << "OpenGL Error at " << msg << ": " << err << std::endl;
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

    void draw(int instances = 1) {
        bind();
        // for (int i = 0; i < instances; i++) {
            glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, nullptr);
        // }
    }
};

class VAO {
protected:
    // GLuint id{};
    std::vector<unsigned> attribs;
    GLsizei stride{};

public:
    VAO() {
        // glGenVertexArrays(1, &id);
    }

    inline void bind() const {
        // glBindVertexArray(id);

        finalize();
    }

    void pushFloat(unsigned qty) {
        attribs.emplace_back(qty);
        stride += sizeof(float) * qty;
    }

    void finalize(int inc = 1) const {
        // bind();
        GLsizei ptr = 0;
        for (GLuint i = 0; i < (attribs.size() * inc); i += inc) {
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attribs[i], GL_FLOAT, GL_FALSE, stride, reinterpret_cast<void *>(ptr));
            ptr += attribs[i] * sizeof(float);
        }
    }

    virtual ~VAO() {
        // glDeleteVertexArrays(1, &id);
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

    inline void bindAttribLoc(GLuint idx, const GLchar *name) const {
        glBindAttribLocation(id, idx, name);
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

    static inline void setFv(UniformLocation in, GLfloat *val, GLsizei num) {
        flushErrors("Pre fv");
        glUniform1fv(in, num, val);
        flushErrors("Post fv");
    }

    static inline void set1i(UniformLocation in, GLint val) {
        glUniform1i(in, val);
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

class Framebuffer {
private:
    GLuint id{};
    GLuint tex{};
    GLuint rbo{};
public:
    int width;
    int height;

    Framebuffer(int width, int height) : width(width), height(height) {
        glGenFramebuffers(1, &id);
        glGenTextures(1, &tex);
        glGenRenderbuffers(1, &rbo);

        bind();

        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        bindTex();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, nullptr);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex, 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw std::runtime_error("Framebuffer incomplete!");
        }
    }

    inline void bind() const {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    inline void bindTex() const {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tex);
    }

    static inline void bindDefault() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    Framebuffer &operator=(Framebuffer &&rhs) noexcept {
        if (&rhs == this) {
            return *this;
        }

        glDeleteFramebuffers(1, &id);
        glDeleteTextures(1, &tex);
        glDeleteRenderbuffers(1, &rbo);

        id = rhs.id;
        tex = rhs.tex;
        rbo = rhs.rbo;

        rhs.rbo = 0;
        rhs.tex = 0;
        rhs.id = 0;


        return *this;
    }

    ~Framebuffer() {
        glDeleteFramebuffers(1, &id);
        glDeleteTextures(1, &tex);
        glDeleteRenderbuffers(1, &rbo);
    }

};

class ALBuf {
private:
    ALuint id{};

    friend class ALSrc;

public:
    explicit ALBuf(const std::string &filename) {
        short *data;
        int channels, sampleRate;
        alGenBuffers(1, &id);
        int len = stb_vorbis_decode_filename(filename.c_str(), &channels, &sampleRate, &data);
        alGenBuffers(1, &id);

        if (channels > 1) {
            alBufferData(id, AL_FORMAT_STEREO16, data, len * 2 * sizeof(short), sampleRate);
        } else {
            alBufferData(id, AL_FORMAT_MONO16, data, len * sizeof(short), sampleRate);
        }
    }

    ~ALBuf() {
        alDeleteBuffers(1, &id);
    }
};


class ALSrc {
private:
    ALuint id{};
public:
    ALSrc() {
        alGenSources(1, &id);
        alSourcef(id, AL_PITCH, 1);
        alSourcef(id, AL_GAIN, 1);
        alSource3f(id, AL_POSITION, -4, -8, 0);
        alSource3f(id, AL_VELOCITY, 1, 1, 1);
        alSourcei(id, AL_LOOPING, AL_TRUE);
    }

    ~ALSrc() {
        alDeleteSources(1, &id);
    }

    void bind(ALBuf *buf) const {
        alSourcei(id, AL_BUFFER, buf->id);
    }

    void play() const {
        alSourcePlay(id);
    }
};