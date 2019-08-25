//
// Created by Grant on 2019-08-23.
//

#pragma once

#ifndef MASON_APPLICATION_H
#define MASON_APPLICATION_H

#include "mason/masonpch.h"

namespace mason {
    class Application;

    class Scene;

    class SceneFactory {
    public:
        virtual Scene *loadScene() = 0;
    };

    class Scene {
    public:
        Application *parent = nullptr;  // Use raw pointers b/c there should only be 1 app obj at any time.

        virtual void enter(int prevScene) {};

        virtual void tick() {};

        virtual void exit(int nextScene) {};
    };

    class Application {
    public:
        int sceneID = -1;
        Scene *currentScene = nullptr;
        std::unordered_map<int, std::shared_ptr<SceneFactory>> scenes;
        bool running = true;

        Application() = default;

        ~Application();

        void start();

        void stop();

        void updateScene(int newScene);

        virtual void pre() {};

        virtual void preScene() {};

        virtual void postScene() {};

        virtual void post() {};
    };
}

#endif //MASON_APPLICATION_H
