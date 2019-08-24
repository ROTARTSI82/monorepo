//
// Created by Grant on 2019-08-22.
//

#include "mason/mason.h"
#include "mason/application.h"
#include "mason/log.h"

class SandboxScene : public mason::Scene {
public:
    void tick() {
        log_warn("Hello! I'm exiting!");
        parent->stop();
    }
};

class SandboxFactory : public mason::SceneFactory {
public:
    mason::Scene *loadScene() {
        log_warn("Loading scene...");
        log_trace("HELLO");
        return new SandboxScene();
    }
};

int main() {
    mason::log::init(true);
    log_warn("Starting!");
    mason::Application *app = new mason::Application();
    app->scenes[0] = std::make_shared<SandboxFactory>();
    app->start();
    delete app;
    log_warn("GOT IT TO THE END WITHOUT ERRORS!");
    return 0;
}
