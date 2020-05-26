// Written by Grant Yang for my 7th Grade CS project.

#define GLFW_INCLUDE_NONE

#include "imgui/imgui.h"
#include "imgui/examples/imgui_impl_glfw.h"
#include "imgui/examples/imgui_impl_opengl2.h"

#include <iostream>
#include <unordered_map>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <GLFW/glfw3.h>
#include <random>

#define TINYOBJLOADER_IMPLEMENTATION

#include <tinyobjloader/tiny_obj_loader.h>

#define GLM_ENABLE_EXPERIMENTAL

#include <glm/gtx/hash.hpp>

#include <al.h>
#include <alc.h>

#include <abstract.cpp>

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
    ALCdevice *alDev = alcOpenDevice(nullptr);
    ALCcontext *alCtx = alcCreateContext(alDev, nullptr);
    alcMakeContextCurrent(alCtx);

    alListener3f(AL_VELOCITY, 1, 1, 1);

    std::random_device seeder;
    std::default_random_engine randEngine(seeder());
    auto radianDist = std::uniform_real_distribution<float>(0, 2 * 3.14159265);

    if (!glfwInit()) {
        throw std::runtime_error("GLFW initialization failed! Aborting!");
    }

    win = glfwCreateWindow(640, 480, "7th Grade Comp Sci Project", nullptr, nullptr);
    if (!win) { throw std::runtime_error("Window creation failed! Aborting!"); }
    glfwMakeContextCurrent(win);
    glfwSwapInterval(1);

    glewExperimental = true;
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
    shaders.bindAttribLoc(0, "pos");
    shaders.bindAttribLoc(1, "inTexCoord");
    shaders.bindAttribLoc(2, "instanceMat");
    shaders.attach(vertShader);
    shaders.attach(fragShader);
    shaders.link();
    shaders.bind();

    std::vector<Vertex> modelVboDat;
    std::vector<unsigned> modelIboDat;
    std::unordered_map<Vertex, unsigned> modelVertMap;

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

            auto iter = std::find(modelVboDat.begin(), modelVboDat.end(), vertex);

            if (iter == modelVboDat.end()) {
                modelVboDat.emplace_back(vertex);
                modelVertMap[vertex] = modelVboDat.size() - 1;
                modelIboDat.emplace_back(modelVboDat.size() - 1);
            } else {
                modelIboDat.emplace_back(modelVertMap[vertex]);
            }
        }
    }

    std::vector<glm::mat4> modelInstanceVboDat;
    modelInstanceVboDat.emplace_back(glm::mat4(1.0f));

    // TODO: Location to play sound: {-64, 8, -64}
    auto modelVbo = GenericBuffer<Vertex, GL_ARRAY_BUFFER>(modelVboDat);
    auto modelIbo = IBO(modelIboDat);
    auto modelTex = Texture("./res/tex/rick.jpg", true);
    auto modelVao = VAO();
    auto modelInstanceVbo = GenericBuffer<glm::mat4, GL_ARRAY_BUFFER>(modelInstanceVboDat);

    modelVbo.bind();
    modelIbo.bind();
    modelTex.bind();
    modelVao.bind();
    modelVao.pushFloat(3);
    modelVao.pushFloat(2);
    modelVao.finalize();
    modelInstanceVbo.bind();
    for (int i = 2; i < 6; i++) {
        glEnableVertexAttribArray(i);
        glVertexAttribPointer(i, 4, GL_FLOAT, GL_FALSE, 16 * sizeof(float), (void *) ((i - 2) * 4 * sizeof(float)));
        glVertexAttribDivisor(i, 1);
    }


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

    auto instanceVboDat = std::vector<glm::mat4>();
    for (int i = 0; i < 4096; i++) {
        auto newMat = glm::eulerAngleYXZ(radianDist(randEngine), radianDist(randEngine), radianDist(randEngine));
        newMat = glm::translate(glm::mat4(1.0f),
                                glm::vec3((i % 16) * 8 - 64, ((i % 256) / 16) * 8 - 64, (i / 256) * 8 - 64)) * newMat;
        instanceVboDat.emplace_back(newMat);
    }
    auto instanceVbo = GenericBuffer<glm::mat4, GL_ARRAY_BUFFER>(instanceVboDat);

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

    vao.bind();
    instanceVbo.bind();
    for (int i = 2; i < 6; i++) {
        glEnableVertexAttribArray(i);
        glVertexAttribPointer(i, 4, GL_FLOAT, GL_FALSE, 16 * sizeof(float), (void *) ((i - 2) * 4 * sizeof(float)));
        glVertexAttribDivisor(i, 1);
    }

    auto postVboDat = std::vector<float>({-1.0f, 1.0f, 0.0f, 1.0f,
                                          -1.0f, -1.0f, 0.0f, 0.0f,
                                          1.0f, -1.0f, 1.0f, 0.0f,
                                          1.0f, 1.0f, 1.0f, 1.0f});
    auto postIboDat = std::vector<unsigned>({0, 1, 2, 0, 2, 3});

    auto postIbo = IBO(postIboDat);
    auto postVbo = VBO(postVboDat);

    VAO postVao;
    postIbo.bind();
    postVbo.bind();
    postVao.bind();
    postVao.pushFloat(2);
    postVao.pushFloat(2);
    postVao.finalize();

    Shader postVert = Shader("./shaders/post.vert", true);
    Shader postFrag = Shader("./shaders/post.frag", false);
    ShaderProgram postShaders;
    postShaders.bindAttribLoc(0, "pos");
    postShaders.bindAttribLoc(1, "inTexCoord");
    postShaders.attach(postVert);
    postShaders.attach(postFrag);
    postShaders.link();
    postShaders.bind();
    UniformLocation postTexSlot = postShaders.getLocation("texSlot");
    UniformLocation kernel = postShaders.getLocation("kernel");
    ShaderProgram::set1i(postTexSlot, 0);
    float kernelArr[9] = {0, 0, 0, 0, 1, 0, 0, 0, 0};
    ShaderProgram::setFv(kernel, kernelArr, 9);

    shaders.bind();
    UniformLocation texSlot = shaders.getLocation("texSlot");
    UniformLocation matM = shaders.getLocation("model");
    UniformLocation matV = shaders.getLocation("view");
    UniformLocation matP = shaders.getLocation("projection");

    ShaderProgram::set1i(texSlot, 0);

    Camera cam = Camera();
    cam.pos = {0, 0, 0};

    Framebuffer postFramebuf = Framebuffer(640, 480);

    // Setup Dear ImGui context
    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGuiIO &io = ImGui::GetIO();
    (void) io;
    //io.ConfigFlags |= ImGuiConfigFlags_NavEnableKeyboard;     // Enable Keyboard Controls
    //io.ConfigFlags |= ImGuiConfigFlags_NavEnableGamepad;      // Enable Gamepad Controls

    // Setup Dear ImGui style
    ImGui::StyleColorsDark();
    //ImGui::StyleColorsClassic();

    // Setup Platform/Renderer bindings
    ImGui_ImplGlfw_InitForOpenGL(win, true);
    ImGui_ImplOpenGL2_Init();

    float fov = 70;

    float mouseSense = 0.0625;
    float speed = 0.125;

    float modelYaw = 0;
    float modelSpinSpeed = 0.0625;

    ALSrc rickSrc;
    ALBuf nevaGonna = ALBuf("./res/rick.ogg");
    rickSrc.bind(&nevaGonna);
    rickSrc.play();

    while (!glfwWindowShouldClose(win)) {
        // Start the Dear ImGui frame
        ImGui_ImplOpenGL2_NewFrame();
        ImGui_ImplGlfw_NewFrame();
        ImGui::NewFrame();

        shaders.bind();
        int width, height;
        glfwGetFramebufferSize(win, &width, &height);
        cam.setProj(fov, static_cast<float>(width) / static_cast<float>(height));
        cam.updateViewMat();

        float ori[6] = {cam.forward.x, cam.forward.y, cam.forward.z,
                        cam.up.x, cam.up.y, cam.up.z};
        alListener3f(AL_POSITION, cam.pos.x, cam.pos.y, cam.pos.z);
        alListenerfv(AL_ORIENTATION, ori);

        ALCenum error;

        error = alGetError();
        while (error != AL_NO_ERROR) {
            throw std::runtime_error(&"OpenAL ERR: "[error]);
            error = alGetError();
        }

        if (width != postFramebuf.width || height != postFramebuf.height) {
            Framebuffer newFramebuf = Framebuffer(width, height);
            postFramebuf = std::move(newFramebuf);
        }

        glViewport(0, 0, width, height);

        glEnable(GL_DEPTH_TEST);
        postFramebuf.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        ShaderProgram::setMat4(matV, cam.getView());
        ShaderProgram::setMat4(matP, cam.getProj());

        ShaderProgram::setMat4(matM, glm::mat4(1.0f));
        tex.bind();
        vbo.bind();
        vao.bind();
        instanceVbo.bind();
        for (int i = 2; i < 6; i++) {
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, 4, GL_FLOAT, GL_FALSE, 16 * sizeof(float), (void *) ((i - 2) * 4 * sizeof(float)));
            glVertexAttribDivisor(i, 1);
        }
        ibo.draw(4096);

        modelYaw += modelSpinSpeed;
        ShaderProgram::setMat4(matM,
                               glm::translate(glm::mat4(1.0f), {4, 1, 0}) *
                               glm::scale(glm::mat4(1.0f), glm::vec3({1, 1, 1}) * 0.1f)
                               * glm::eulerAngleYXZ(modelYaw, 0.0f, 0.0f)
        );
        modelTex.bind();

        modelVbo.bind();
        modelVao.bind();
        modelInstanceVbo.bind();
        for (int i = 2; i < 6; i++) {
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, 4, GL_FLOAT, GL_FALSE, 16 * sizeof(float), (void *) ((i - 2) * 4 * sizeof(float)));
            glVertexAttribDivisor(i, 1);
        }

        modelIbo.draw(1);


        ImGui::Begin("Stuff");

        ImGui::Text("Position: [%f, %f, %f]", cam.pos.x, cam.pos.y, cam.pos.z);
        ImGui::Text("Euler Angle: [%f, %f, %f]", cam.euler.x, cam.euler.y, cam.euler.z);
        ImGui::Text("Application average %.3f ms/frame (%.1f FPS)", 1000.0f / ImGui::GetIO().Framerate,
                    ImGui::GetIO().Framerate);
        for (int i = 0; i < 9; i++) {
            std::string name = "Kernel ";
            name += std::to_string(i);
            ImGui::SliderFloat(name.c_str(), &kernelArr[i], -9, 9);
        }
        ImGui::End();

        glDisable(GL_DEPTH_TEST);
        Framebuffer::bindDefault();
        ImGui::Render();
        glClear(GL_COLOR_BUFFER_BIT);

        postShaders.bind();
        ShaderProgram::setFv(kernel, &kernelArr[0], 9);
        postFramebuf.bindTex();
        postVao.bind();
        postIbo.draw();
        ImGui_ImplOpenGL2_RenderDrawData(ImGui::GetDrawData());

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

    alcDestroyContext(alCtx);
    alcCloseDevice(alDev);
    return 0;
}
