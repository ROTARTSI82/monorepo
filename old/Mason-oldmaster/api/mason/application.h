//
// Created by Grant on 2019-08-23.
//

#pragma once

#ifndef MASON_APPLICATION_H
#define MASON_APPLICATION_H

#include "mason/masonpch.h"

namespace mason {
    class LayerStack;

    class UpdaterGroup;
    class Application;
    class Scene;

    class Layer {
    public:
        Layer() = default;

        ~Layer() = default;

        bool active = false;

        virtual void updateLogic(UpdaterGroup *up, unsigned int thread) {};

        virtual void updateRender(UpdaterGroup *up, unsigned int thread) {};

        virtual void updateEvent(Event *event) {};

//        virtual void updateNetwork(raknet::packet* packet) {};

        virtual void onEnable() {};

        virtual void onDisable() {};

        void enable();

        void disable();
    };

    class UpdaterGroup {
    public:
        static void threadFunc(UpdaterGroup *group, unsigned int threadNum);

        std::vector<std::unique_ptr<std::thread>> threads;

        LayerStack *stack = nullptr;
        unsigned int numThreads = 1;
        unsigned int delay = 0;
        volatile bool isRunning = false;


        void (*tickFunc)(UpdaterGroup *, unsigned int) = nullptr;

        UpdaterGroup(void(*tfunc)(UpdaterGroup *, unsigned int), unsigned int nthreads, unsigned int del,
                     LayerStack *par);

        ~UpdaterGroup();

        void start();

        void stop();
    };

    class LayerStack : public std::vector<Layer *> {
    public:
        static void eventTick(UpdaterGroup *g, unsigned int tn);

        std::vector<UpdaterGroup *> updaterGroups;

        Scene *scene = nullptr;  // Is set in mason::Scene's constructor

        LayerStack() = default;

        ~LayerStack();

        void addUpdaterGroup(void(*func)(UpdaterGroup *, unsigned int), unsigned int threads, unsigned int del);

        void startUpdaters();

        void stopUpdaters();

        void setLayerActive(int index, bool active);
    };

    class Scene {
    public:
        Application *app = nullptr;  // Use raw pointers b/c there should only be 1 app obj at any time.
        LayerStack *stack = new LayerStack();;

        Scene();

        ~Scene();

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

        volatile int requestedScene = -1;

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
