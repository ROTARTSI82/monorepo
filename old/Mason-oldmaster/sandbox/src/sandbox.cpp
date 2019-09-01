//
// Created by Grant on 2019-08-22.
//

#include "mason/mason.h"
#include "mason/gl/window.h"
#include "mason/application.h"
#include "mason/gl/camera.h"
#include "mason/gl/gl_objects.h"
#include "mason/log.h"

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

class SandboxApplication : public mason::Application {
public:
    mason::gl::GLWindow *window;

    SandboxApplication() {
        window = new mason::gl::GLWindow();
        window->setClearColor(0.25f, 0.25f, 1, 1);
    }

    ~SandboxApplication() {
        delete window;
    }

    void pre() override {};

    void tick() override {};

    void post() override {};
};

SandboxApplication *globalApp;

class SandboxLayer : public mason::Layer {
public:

    mason::gl::GameObject *cube;
    mason::gl::GameObject *skybox;
    mason::gl::Camera *cam;

    float sensitivity = 3;
    float speed = 3;

    float deltaTime;
    double lastFrame;

    SandboxLayer() {
        cam = new mason::gl::Camera(glm::vec3(0, 0, 0), glm::vec2(0, 0), globalApp->window);
        MASON_INFO("Camera constructed")

        float x = 0.5f;
        float y = 0.0f;
        float pos[] = {
                -x, x, x, 0, 0, // 0
                x, x, x, 1, 0, // 1
                x, -x, x, 1, 1, //2
                -x, -x, x, 0, 1, // 3

                -x, x, -x, 0, 0, // 4
                x, x, -x, 1, 0,// 5
                x, -x, -x, 1, 1, // 6
                -x, -x, -x, 0, 1, // 7

                x, -x, x, 0, 1, // 8
                x, x, x, 0, 0, // 9
                x, x, x, 1, 1, // 10
                -x, x, x, 0, 1, // 11
                -x, x, x, 1, 0, // 12
                -x, -x, x, 1, 1, // 13

                -x, -x, x, 0, 0, // 14
                x, -x, x, 1, 0, // 15

        };

        unsigned int index[] = {
                0, 1, 2, 3,
                7, 6, 5, 4,

                9, 5, 6, 8, // (x, x; 0, 0), (x, x; 1, 0), (x, -x; 1, 1), (x, -x; 0, 1)
                4, 5, 10, 11, // (-x, x, -x);(0, 0), (x, x, -x);(1, 0), (x, x, x);(1, 1), (-x, x, x);(0, 1)
                4, 12, 13, 7, // (-x, x, x);(1, 0), (-x, -x, x);(1, 1);
                14, 15, 6, 7 // (-x, -x, x);(0, 0), (x, -x, x);(1, 0)
        };

        unsigned int inwardIndex[]{
                3, 2, 1, 0,
                4, 5, 6, 7,

                8, 6, 5, 9, // (x, x; 0, 0), (x, x; 1, 0), (x, -x; 1, 1), (x, -x; 0, 1)
                11, 10, 5, 4, // (-x, x, -x);(0, 0), (x, x, -x);(1, 0), (x, x, x);(1, 1), (-x, x, x);(0, 1)
                7, 13, 12, 4, // (-x, x, x);(1, 0), (-x, -x, x);(1, 1);
                7, 6, 15, 14 // (-x, -x, x);(0, 0), (x, -x, x);(1, 0)
        };

        mason::gl::VertexArray *arr = new mason::gl::VertexArray();
        arr->bind();
        mason::gl::VertexBuffer *buff = new mason::gl::VertexBuffer(pos, GL_STATIC_DRAW, sizeof(pos));
        mason::gl::VBLayout *layout = new mason::gl::VBLayout();
        layout->addAttribute(3, GL_FLOAT, GL_FALSE);
        layout->addAttribute(2, GL_FLOAT, GL_FALSE);
        buff->setLayout(layout);
        mason::gl::IndexBuffer *ibo = new mason::gl::IndexBuffer(index, GL_STATIC_DRAW, 24, GL_UNSIGNED_INT);
        mason::gl::IndexBuffer *skyibo = new mason::gl::IndexBuffer(inwardIndex, GL_STATIC_DRAW, 24, GL_UNSIGNED_INT);
        mason::gl::ShaderProgram *shader = new mason::gl::ShaderProgram(
                "/Users/Grant/Desktop/CLion/Mason/sandbox/res/shaders");
        shader->bind();
        shader->setUniform1i("u_Texture", 0);
        mason::gl::Image *img = new mason::gl::Image("/Users/Grant/Desktop/CLion/Mason/sandbox/res/textures/tex1.png");
        mason::gl::Image *skyImg = new mason::gl::Image(
                "/Users/Grant/Desktop/CLion/Mason/sandbox/res/textures/tex0.png");
        mason::gl::Texture2D *tex = new mason::gl::Texture2D(img);
        tex->genMipmaps();
        mason::gl::Texture2D *skyTex = new mason::gl::Texture2D(skyImg);
        skyTex->genMipmaps();
        delete skyImg;
        delete img;

        mason::gl::Model *md = new mason::gl::Model();
        md->vbo = buff;
        md->drawMode = GL_QUADS;
        md->ibo = ibo;
        md->vao = arr;

        mason::gl::Model *skymodel = new mason::gl::Model();
        skymodel->vbo = buff;
        skymodel->drawMode = GL_QUADS;
        skymodel->ibo = skyibo;
        skymodel->vao = arr;

        cube = new mason::gl::GameObject();
        cube->model = md;
        cube->texture = tex;
        cube->preRender = []() {};
        cube->shader = shader;
        cube->transform = glm::mat4(1.0f);

        skybox = new mason::gl::GameObject();
        skybox->model = skymodel;
        skybox->texture = skyTex;
        skybox->preRender = []() {};
        skybox->shader = shader;
        skybox->transform = glm::scale(glm::mat4(1.0), glm::vec3(5, 5, 5));

        globalApp->window->renderQueue.clear();
        globalApp->window->renderQueue.push_back(cube);
        globalApp->window->renderQueue.push_back(skybox);
    }

    void updateLogic(mason::UpdaterGroup *gp, unsigned int tn) override {
        globalApp->window->projection = cam->getProjection();
        globalApp->window->view = cam->getView();

        double now = glfwGetTime();
        deltaTime = now - lastFrame;
        lastFrame = now;

        if (glfwGetKey(globalApp->window->win, GLFW_KEY_R) == GLFW_PRESS) {
            globalApp->window->renderQueue.clear(); // So we don't try to draw this scene's objects in the next scene.
            globalApp->requestedScene = 0;
            globalApp->loadScene = true;
        }

        if (glfwGetKey(globalApp->window->win, GLFW_KEY_W) == GLFW_PRESS) {
            cam->move(speed, 0, deltaTime);
        }
// Move backward
        if (glfwGetKey(globalApp->window->win, GLFW_KEY_S) == GLFW_PRESS) {
            cam->move(-speed, 0, deltaTime);
        }
// Strafe right
        if (glfwGetKey(globalApp->window->win, GLFW_KEY_D) == GLFW_PRESS) {
            cam->move(0, speed, deltaTime);
        }
// Strafe left
        if (glfwGetKey(globalApp->window->win, GLFW_KEY_A) == GLFW_PRESS) {
            cam->move(0, -speed, deltaTime);
        }

        if (glfwGetKey(globalApp->window->win, GLFW_KEY_UP) == GLFW_PRESS) {
            cam->look(glm::vec2(0, sensitivity), deltaTime);
        }
// Move backward
        if (glfwGetKey(globalApp->window->win, GLFW_KEY_DOWN) == GLFW_PRESS) {
            cam->look(glm::vec2(0, -sensitivity), deltaTime);
        }
// Strafe right
        if (glfwGetKey(globalApp->window->win, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            cam->look(glm::vec2(-sensitivity, 0), deltaTime);
        }
// Strafe left
        if (glfwGetKey(globalApp->window->win, GLFW_KEY_LEFT) == GLFW_PRESS) {
            cam->look(glm::vec2(sensitivity, 0), deltaTime);
        }

        if (glfwGetKey(globalApp->window->win, GLFW_KEY_SPACE) == GLFW_PRESS) {
            cam->position.y += (deltaTime * speed);
        }

        if (glfwGetKey(globalApp->window->win, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            cam->position.y += -(deltaTime * speed);
        }

//        log_warn("scene {} logic update {} thread {} layer {}", scene, count, tn, num);

    }

    ~SandboxLayer() {
//        for (mason::gl::GLWindow* win : slaves) {
//            delete win;
//        }
        delete cube->model->vbo->layout;
        delete cube->model->vbo;
        delete cube->model->vao;
        delete cube->model->ibo;
        delete cube->model;
        delete cube->texture;
        delete cube->shader;
        delete cube;

        delete skybox->model->ibo;
        delete skybox->model;
        delete skybox->texture;
        delete skybox;

        delete cam;
    }
};

void func(mason::UpdaterGroup *g, unsigned int tn) {
    for (mason::Layer *l : *(g->stack)) {
        l->updateLogic(g, tn);
    }
}

class SandboxScene : public mason::Scene {
public:

    SandboxScene() {

    }

    ~SandboxScene() {

    }

    void enter(int prev) override {
        stack->push_back(new SandboxLayer());
        stack->addUpdaterGroup(func, 1, 0);
        stack->startUpdaters();
    }

    void tick() override {
        globalApp->window->clear();
        globalApp->window->drawAll();
        globalApp->window->flip();

        if (globalApp->window->shouldClose()) {
            globalApp->stop();
        }
//        log_warn("Hello! Tick is executing!");
    }
};

mason::Scene *loadScene() {
//    log_warn("Loading scene...");
//    log_trace("nou");
    return new SandboxScene();
}

int main() {
    {
        mason::gl::Camera testCam = mason::gl::Camera(glm::vec3(0, 0, 0), glm::vec2(0, 0), nullptr);
    }

    mason::log::init(true);
            MASON_WARN("Starting!");
    globalApp = new SandboxApplication();
    globalApp->scenes[0] = loadScene;
    globalApp->start();
    delete globalApp;

    glfwTerminate();
    MASON_WARN("GOT IT TO THE END WITHOUT ERRORS!");
    return 0;
}
