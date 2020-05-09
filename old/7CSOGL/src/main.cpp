#include <iostream>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <GLFW/glfw3.h>

#include <buffers.cpp>

GLFWwindow *win{};

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

        ibo.draw(4096);
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
