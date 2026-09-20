//
// Created by Grant on 9/23/19.
//

#pragma once

#ifndef __MASON_APP_NODE_H
#define __MASON_APP_NODE_H

#include <vector>
#include "mason/gl/gl_event.h"

#include "mason/layer.h"

namespace mason {
    namespace gl {
        struct gl_event;
    }

    class layer;

    class app_node;

    class app_node {
    private:
        unsigned short insert_index = 0;
    public:
        app_node *parent = nullptr;

        std::vector<mason::layer *> layer_stack;
        std::vector<mason::app_node *> active_children;

        app_node() = default;

        virtual ~app_node();

        virtual void on_activate() = 0;

        virtual void on_deactivate() = 0;

        void update(int thread);

        // NOTE: Any layers pushed to the app_node WILL BE DESTROYED in the app_node's destructor.
        void push_underlay(mason::layer *obj);

        bool handle_event(mason::gl::gl_event *ev);

        void remove_underlay(const int &index);

        void push_overlay(mason::layer *obj);

        void remove_overlay(const int &index);

        void push_child(mason::app_node *obj);

        void remove_child(const int &index);
    };
}

#endif //__MASON_APP_NODE_H
