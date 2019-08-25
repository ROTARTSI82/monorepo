//
// Created by Grant on 2019-08-22.
//

#include "mason/mason.h"
#include "mason/application.h"
#include "mason/log.h"

class SandboxLayer : public mason::Layer {
public:
    int num = 0;
    int count = 0;

    SandboxLayer(int n) : num(n) {

    }

    void updateLogic(unsigned int tn) override {
        log_warn("logic update {} thread {} layer {}", count, tn, num);
        count++;
    }
};

void func(mason::UpdaterGroup *g, unsigned int tn) {
    for (mason::Layer *l : *(g->stack)) {
        l->updateLogic(tn);
    }
}

class SandboxScene : public mason::Scene {
public:
    void enter(int prev) override {
        stack->push_back(new SandboxLayer(1));
        stack->push_back(new SandboxLayer(2));
        stack->addUpdaterGroup(func, 2, 1000);
        stack->startUpdaters();
    }
    void tick() override {
//        log_warn("Hello! Tick is executing!");
    }
};

mason::Scene *loadScene() {
    log_warn("Loading scene...");
    log_trace("nou");
    return new SandboxScene();
}

int main() {
    mason::log::init(true);
    log_warn("Starting!");
    mason::Application *app = new mason::Application();
    app->scenes[0] = loadScene;
    app->start();
    delete app;
    log_warn("GOT IT TO THE END WITHOUT ERRORS!");
    return 0;
}
