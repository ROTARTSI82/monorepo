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

    glewExperimental = GL_TRUE;
    if (glewInit() != GLEW_OK) {
        throw std::runtime_error("GLEW initialization failed! Aborting!");
    }

    const GLubyte* renderer = glGetString(GL_RENDERER);
    const GLubyte* version = glGetString(GL_VERSION);
    std::cout << "Initialized OpenGL " << version << " with renderer " << renderer << std::endl;

    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LESS);

    auto vertShader = Shader("./shaders/default.vert", true);
    auto fragShader = Shader("./shaders/default.frag", false);

    ShaderProgram sProgram;
    sProgram.attach(vertShader);
    sProgram.attach(fragShader);
    sProgram.link();
    sProgram.bind();

    auto vboDat = std::vector<float>({0.5, 0.5, 0, 0.5, -0.5, 0, -0.5, -0.5, 0, -0.5, 0.5, 0});
    VBO vbo(vboDat);

    auto iboDat = std::vector<unsigned>({0, 1, 3, 2, 3, 1});
    IBO ibo(iboDat);

    auto vao = VAO();
    vao.pushFloat(3);

    vbo.bind();
    ibo.bind();
    vao.bind();
    vao.finalize();

    while (!glfwWindowShouldClose(win)) {
        int width, height;
        glfwGetFramebufferSize(win, &width, &height);
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        ibo.draw();
        glfwSwapBuffers(win);
        glfwPollEvents();
        flushErrors("null");
    }

    glfwDestroyWindow(win);
    glfwTerminate();
    return 0;
}
