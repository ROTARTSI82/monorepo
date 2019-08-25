//
// Created by Grant on 2019-08-23.
//

#pragma once

#ifndef MASON_APPLICATION_H
#define MASON_APPLICATION_H

#include "mason/masonpch.h"
#include "mason/layers.h"

namespace mason {
    class Application;

    class Scene;

    class Scene {
    public:
        Application *app = nullptr;  // Use raw pointers b/c there should only be 1 app obj at any time.
        mason::LayerStack *stack = new LayerStack();

        ~Scene() {
            delete stack;
        };

        virtual void enter(int prevScene) {};

        virtual void tick() {};

        virtual void exit(int nextScene) {};
    };

    class Application {
    public:
        int sceneID = -1;
        Scene *currentScene = nullptr;
        // An unordered map of function pointers...
        std::unordered_map<int, Scene *(*)()> scenes;
        volatile bool running = true;

        Application() = default;

        ~Application();

        void start();

        void stop();

        void updateScene(int newScene);

        virtual void pre() {};

        virtual void tick() {};

        virtual void post() {};
    };
}

#endif //MASON_APPLICATION_H
