//
// Created by Grant on 2019-08-22.
//

#include "mason/mason.h"
#include "mason/gl/window.h"
#include "mason/application.h"
#include "mason/log.h"

class SandboxApplication : public mason::Application {
public:
    mason::gl::GLWindow *window;

    SandboxApplication() {
        window = new mason::gl::GLWindow();
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
    int num = 0;
    int scene = 0;
    int count = 0;

    SandboxLayer(int n, int sn) : num(n), scene(sn) {

    }

    void updateLogic(mason::UpdaterGroup *gp, unsigned int tn) override {
//        log_warn("scene {} logic update {} thread {} layer {}", scene, count, tn, num);
        count++;
        if (count > 5) {
            gp->stack->scene->app->requestedScene = (scene == 1 ? 1 : 0);
        }
    }

    ~SandboxLayer() {
//        for (mason::gl::GLWindow* win : slaves) {
//            delete win;
//        }
    }
};

void func(mason::UpdaterGroup *g, unsigned int tn) {
    for (mason::Layer *l : *(g->stack)) {
        l->updateLogic(g, tn);
    }
}

class SandboxScene : public mason::Scene {
public:
    int num;

    SandboxScene(int n) : num(n) {}

    void enter(int prev) override {
        stack->push_back(new SandboxLayer(1, num));
        stack->push_back(new SandboxLayer(2, num));
        stack->addUpdaterGroup(func, 1, 0);
        stack->startUpdaters();
    }
    void tick() override {
        globalApp->window->clear();
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
    return new SandboxScene(1);
}

mason::Scene *scene2() {
    return new SandboxScene(2);
};

int main() {
    mason::log::init(true);
            MASON_WARN("Starting!");
    globalApp = new SandboxApplication();
    globalApp->scenes[0] = loadScene;
    globalApp->scenes[1] = scene2;
    globalApp->start();
    delete globalApp;
    MASON_WARN("GOT IT TO THE END WITHOUT ERRORS!");

    glfwTerminate();
    return 0;
}
