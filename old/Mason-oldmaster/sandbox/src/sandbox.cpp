//
// Created by Grant on 2019-08-22.
//

#include "mason/mason.h"
#include "mason/application.h"
#include <iostream>

class SandboxScene : public mason::Scene {
public:
    void tick() {
        std::cout << "Hello! Now I\'m exiting! parent=" << parent << std::endl;
        parent->stop();
    }
};

class SandboxFactory : public mason::SceneFactory {
public:
    mason::Scene *loadScene() {
        std::cout << "Making new sandbox scene!" << std::endl;
        return new SandboxScene();
    }
};

int main() {
    mason::Application *app = new mason::Application();
    app->scenes[0] = std::make_shared<SandboxFactory>();
    app->updateScene(0);
    app->start();
    delete app;
    std::cout << "YES WE GOT IT TO THE END" << std::endl;
    return 0;
}
