#include <iostream>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <GLFW/glfw3.h>

#define TINYOBJLOADER_IMPLEMENTATION

#include <tinyobjloader/tiny_obj_loader.h>

#define GLM_ENABLE_EXPERIMENTAL

#include <glm/gtx/hash.hpp>

#include <buffers.cpp>

GLFWwindow *win{};

struct Vertex {
    glm::vec3 pos;
    glm::vec2 uv;

    bool operator==(const Vertex &rhs) const {
        return pos == rhs.pos && uv == rhs.uv;
    }
};

namespace std {
    template<>
    struct hash<Vertex> {
        size_t operator()(Vertex const &vertex) const {
            return hash<glm::vec3>()(vertex.pos) ^ hash<glm::vec2>()(vertex.uv);
        }
    };
}


int main() {

    if (!glfwInit()) {
        throw std::runtime_error("GLFW initialization failed! Aborting!");
    }

    win = glfwCreateWindow(640, 480, "7th Grade Comp Sci Project", nullptr, nullptr);
    if (!win) { throw std::runtime_error("Window creation failed! Aborting!"); }
    glfwMakeContextCurrent(win);
    glfwSwapInterval(1);

    if (glewInit() != GLEW_OK) {
        throw std::runtime_error("GLEW initialization failed! Aborting!");
    }

    const GLubyte *renderer = glGetString(GL_RENDERER);
    const GLubyte *version = glGetString(GL_VERSION);
    std::cout << "Initialized OpenGL " << version << " with renderer " << renderer << std::endl;

    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LESS);

    glEnable(GL_CULL_FACE);

    auto vertShader = Shader("./shaders/default.vert", true);
    auto fragShader = Shader("./shaders/default.frag", false);

    ShaderProgram shaders;
    shaders.attach(vertShader);
    shaders.attach(fragShader);
    shaders.link();
    shaders.bind();

    std::vector<Vertex> rickVboDat;
    std::vector<unsigned> rickIboDat;
    std::unordered_map<Vertex, unsigned> rickVertMap;

    tinyobj::attrib_t attrib;
    std::vector<tinyobj::shape_t> shapes;
    std::vector<tinyobj::material_t> materials;
    std::string warn, err;

    if (!tinyobj::LoadObj(&attrib, &shapes, &materials, &warn, &err, "./res/obj/rick.obj")) {
        throw std::runtime_error(warn + err);
    }

    for (const auto &shape : shapes) {
        for (const auto &index : shape.mesh.indices) {
            Vertex vertex{};
            vertex.uv.x = attrib.texcoords[2 * index.texcoord_index + 0];
            vertex.uv.y = 1.0f - attrib.texcoords[2 * index.texcoord_index + 1];

            vertex.pos.x = attrib.vertices[3 * index.vertex_index + 0];
            vertex.pos.y = attrib.vertices[3 * index.vertex_index + 1];
            vertex.pos.z = attrib.vertices[3 * index.vertex_index + 2];

            auto iter = std::find(rickVboDat.begin(), rickVboDat.end(), vertex);

            if (iter == rickVboDat.end()) {
                rickVboDat.emplace_back(vertex);
                rickVertMap[vertex] = rickVboDat.size() - 1;
                rickIboDat.emplace_back(rickVboDat.size() - 1);
            } else {
                rickIboDat.emplace_back(rickVertMap[vertex]);
            }
        }
    }

    // TODO: Location to play sound: {-64, 8, -64}
    auto rickVbo = GenericBuffer<Vertex, GL_ARRAY_BUFFER>(rickVboDat);
    auto rickIbo = IBO(rickIboDat);
    auto rickTex = Texture("./res/tex/rick.jpg");
    auto rickVao = VAO();

    rickVbo.bind();
    rickIbo.bind();
    rickTex.bind();
    rickVao.bind();
    rickVao.pushFloat(3);
    rickVao.pushFloat(2);
    rickVao.finalize();


    auto vboDat = std::vector<float>({
                                             -1, 1, -1, 0, 2 / 3.0f,
                                             1, 1, -1, 1, 2 / 3.0f,
                                             1, 1, 1, 1, 1.0,
                                             -1, 1, 1, 0, 1.0,

                                             -1, -1, -1, 0, 1 / 3.0f,
                                             1, -1, -1, 1, 1 / 3.0f,
                                             1, -1, 1, 1, 2 / 3.0f,
                                             -1, -1, 1, 0, 2 / 3.0f,

                                             -1, 1, -1, 0, 0,
                                             1, 1, -1, 1, 0,
                                             1, -1, -1, 1, 1 / 3.0f,
                                             -1, -1, -1, 0, 1 / 3.0f,

                                             -1, 1, 1, 0, 0,
                                             1, 1, 1, 1, 0,
                                             1, -1, 1, 1, 1 / 3.0f,
                                             -1, -1, 1, 0, 1 / 3.0f,

                                             1, 1, -1, 1, 0,
                                             1, -1, -1, 1, 1 / 3.0f,
                                             1, -1, 1, 0, 1 / 3.0f,
                                             1, 1, 1, 0, 0,

                                             -1, 1, -1, 1, 0,
                                             -1, -1, -1, 1, 1 / 3.0f,
                                             -1, -1, 1, 0, 1 / 3.0f,
                                             -1, 1, 1, 0, 0
                                     });
    VBO vbo(vboDat);

    auto iboDat = std::vector<unsigned>({2, 1, 0, 2, 0, 3, // top
                                         4, 5, 6, 7, 4, 6, // Bottom
                                         8, 9, 10, 11, 8, 10,
                                         14, 13, 12, 14, 12, 15,
                                         18, 17, 16, 18, 16, 19,
                                         20, 21, 22, 23, 20, 22
                                        });
    IBO ibo(iboDat);

    auto vao = VAO();
    vao.pushFloat(3); // Vertex pos
    vao.pushFloat(2); // Texture coords (UV)

    auto tex = Texture("./res/tex/grass_texture.png");

    tex.bind();
    vbo.bind();
    ibo.bind();
    vao.bind();
    vao.finalize();

    UniformLocation texSlot = shaders.getLocation("tex");
    UniformLocation matM = shaders.getLocation("model");
    UniformLocation matV = shaders.getLocation("view");
    UniformLocation matP = shaders.getLocation("projection");

    ShaderProgram::set1i(texSlot, 0);
    ShaderProgram::setMat4(matM, glm::translate(glm::mat4(1.0f), {0, 0, 0}));

    Camera cam = Camera();
    cam.pos = {0, 0, 0};

    float fov = 70;

    float mouseSense = 0.0625;
    float speed = 0.125;

    while (!glfwWindowShouldClose(win)) {
        int width, height;
        glfwGetFramebufferSize(win, &width, &height);
        cam.setProj(fov, static_cast<float>(width) / static_cast<float>(height));
        cam.updateViewMat();

        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        ShaderProgram::setMat4(matV, cam.getView());
        ShaderProgram::setMat4(matP, cam.getProj());

        tex.bind();
        vbo.bind();
        vao.bind();
        ibo.bind();
        ibo.draw(4096);

        rickVao.bind();
        rickVbo.bind();
        rickIbo.bind();
        rickTex.bind();
        rickIbo.draw(1);

        glfwSwapBuffers(win);
        glfwPollEvents();
        flushErrors("null");

        if (glfwGetKey(win, GLFW_KEY_UP) == GLFW_PRESS) {
            cam.euler.x -= mouseSense;
        }
        if (glfwGetKey(win, GLFW_KEY_DOWN) == GLFW_PRESS) {
            cam.euler.x += mouseSense;
        }
        if (glfwGetKey(win, GLFW_KEY_LEFT) == GLFW_PRESS) {
            cam.euler.y -= mouseSense;
        }
        if (glfwGetKey(win, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            cam.euler.y += mouseSense;
        }
        if (glfwGetKey(win, GLFW_KEY_W) == GLFW_PRESS) {
            cam.pos += cam.forward * speed;
        }
        if (glfwGetKey(win, GLFW_KEY_A) == GLFW_PRESS) {
            cam.pos += cam.right * speed;
        }
        if (glfwGetKey(win, GLFW_KEY_S) == GLFW_PRESS) {
            cam.pos -= cam.forward * speed;
        }
        if (glfwGetKey(win, GLFW_KEY_D) == GLFW_PRESS) {
            cam.pos -= cam.right * speed;
        }
        if (glfwGetKey(win, GLFW_KEY_SPACE) == GLFW_PRESS) {
            cam.pos -= glm::vec3({0, speed, 0});
        }
        if (glfwGetKey(win, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            cam.pos += glm::vec3({0, speed, 0});
        }

    }

    glfwDestroyWindow(win);
    glfwTerminate();
    return 0;
}
