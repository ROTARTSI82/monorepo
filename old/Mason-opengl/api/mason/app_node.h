//
// Created by Grant on 9/23/19.
//

#pragma once

#ifndef MASONSHARE_APP_NODE_H
#define MASONSHARE_APP_NODE_H

#include <vector>

#include "mason/layer.h"

namespace mason {
    class app_node {
    private:
        unsigned short insert_index = 0;
    public:
        app_node *parent = nullptr;

        std::vector<layer *> layer_stack;
        std::vector<app_node *> active_children;

        app_node() = default;

        virtual ~app_node();

        virtual void on_activate() = 0;

        virtual void on_deactivate() = 0;

        void update(int thread);

        // NOTE: Any layers pushed to the app_node WILL BE DESTROYED in the app_node's destructor.
        void push_underlay(layer *obj);

        void remove_underlay(const int &index);

        void push_overlay(layer *obj);

        void remove_overlay(const int &index);

        void push_child(app_node *obj);

        void remove_child(const int &index);
    };
}

#endif //MASONSHARE_APP_NODE_H
