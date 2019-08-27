//
// Created by Grant on 2019-08-22.
//

#include "mason/mason.h"
#include "mason/application.h"
#include "mason/log.h"

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
        stack->addUpdaterGroup(func, 5, 0);
        stack->startUpdaters();
    }
    void tick() override {
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
    mason::Application *app = new mason::Application();
    app->scenes[0] = loadScene;
    app->scenes[1] = scene2;
    app->start();
    delete app;
            MASON_WARN("GOT IT TO THE END WITHOUT ERRORS!");
    return 0;
}
