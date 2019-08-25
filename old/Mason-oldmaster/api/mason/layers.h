//
// Created by Grant on 2019-08-24.
//
#pragma once

#ifndef MASON_LAYERS_H
#define MASON_LAYERS_H

#include "mason/masonpch.h"
#include "mason/event.h"

namespace mason {
    class LayerStack;

    class Layer {
    public:
        Layer() = default;

        ~Layer() = default;

        bool active = false;

        virtual void updateLogic(unsigned int thread) {};

        virtual void updateRender(unsigned int thread) {};

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

        std::thread *threads = nullptr;
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

        LayerStack() = default;

        ~LayerStack();

        void addUpdaterGroup(void(*func)(UpdaterGroup *, unsigned int), unsigned int threads, unsigned int del);

        void startUpdaters();

        void stopUpdaters();

        void setLayerActive(int index, bool active);
    };
}

#endif //MASON_LAYERS_H
