//
// Created by Grant on 2019-08-23.
//

#include "mason/application.h"

namespace mason {
    Application::~Application() {
        currentScene->exit(-1);
        delete currentScene;
    }

    void Application::start() {
        updateScene(0);
        pre();
        while (running) {
            preScene();
            currentScene->tick();
            postScene();
        }
        post();
    }

    void Application::stop() {
        running = false;
    }

    void Application::updateScene(int newScene) {
        // TODO: Add proper logging here for when currentScene is null
        if (currentScene != nullptr) {
            currentScene->exit(newScene);
        }

        delete currentScene;
        currentScene = scenes[newScene]->loadScene();
        currentScene->parent = this;

        if (currentScene == nullptr) {
            std::cerr << "loadScene() returned null on scene " << newScene << std::endl;
        }
        currentScene->enter(sceneID);
        sceneID = newScene;
    }
}
