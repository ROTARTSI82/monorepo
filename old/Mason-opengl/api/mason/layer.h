//
// Created by Grant on 9/23/19.
//

#pragma once

#ifndef MASONSHARE_LAYER_H
#define MASONSHARE_LAYER_H

namespace mason {
    class app_node;

    class layer {
    public:
        app_node *parent_app = nullptr;

        layer() = default;

        virtual ~layer() = default;

        virtual void on_attach() = 0;

        virtual void on_detach() = 0;

        virtual void on_update(int thread) = 0;
    };
}

#endif //MASONSHARE_LAYER_H
