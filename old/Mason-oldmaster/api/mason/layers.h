//
// Created by Grant on 2019-08-24.
//
#pragma once

#ifndef MASON_LAYERS_H
#define MASON_LAYERS_H

#include "mason/masonpch.h"
#include "mason/event.h"
#include "thread.h"

namespace mason {
    class LayerStack;

    class Layer {
    public:
        Layer() = default;

        ~Layer() = default;

        bool active = true;

        virtual void update() {};

        virtual void updateEvent(Event *event) {};

//        virtual void onAttach(LayerStack* parent) {};
//        virtual void onDetach(LayerStack* parent) {};

        virtual void onEnable() {};

        virtual void onDisable() {};

        void enable();

        void disable();
    };

    class AsyncEventHandler : public Timer {
    public:
        LayerStack *lsparent;

        explicit AsyncEventHandler(LayerStack *parent, unsigned int wait) : lsparent(parent) {
            delay = wait;
        };

        void tick() const override;
    };

    class LayerStack : public std::vector<std::shared_ptr<Layer>> {
    public:
        std::unique_ptr<AsyncEventHandler> eventHandler = std::make_unique<AsyncEventHandler>(this, 5);

        LayerStack();

        void setLayerActive(int index, bool active);

        void update();
    };
}

#endif //MASON_LAYERS_H
