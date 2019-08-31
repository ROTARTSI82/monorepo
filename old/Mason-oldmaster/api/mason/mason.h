#pragma once

#ifndef MASON_MASON_H
#define MASON_MASON_H

#include "mason/masonpch.h"

#define MASON_DEBUG_MODE

namespace mason {
    extern bool debugModeActive;

    struct GameObject {

    };

    enum API {
        OPENGL, AUTO_BY_PLATFORM, VULKAN, METAL, DIRECTX, ALLEGRO, MARMALADE,
    };

    class Window {
    public:
        std::vector<GameObject> renderQueue;

        Window() = default;

        virtual ~Window() = default;

        virtual void submitRender() = 0;

        virtual void bind() = 0;

        virtual bool shouldClose() = 0;

        virtual void asyncSubmitRender() = 0;

        virtual void clear() = 0;

        virtual void flip() = 0;

        virtual void destroy() = 0;
    };
}

namespace mason::log {}

#ifdef MASON_DEBUG_MODE
bool mason::debugModeActive = true;
#else
bool mason::debugModeActive = false;
#endif

#endif //MASON_MASON_H
