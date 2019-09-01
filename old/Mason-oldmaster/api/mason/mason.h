#pragma once

#ifndef MASON_MASON_H
#define MASON_MASON_H

#include "mason/masonpch.h"

#define MASON_DEBUG_MODE

namespace mason {
    extern bool debugModeActive;

    enum API {
        OPENGL, AUTO_BY_PLATFORM, VULKAN, METAL, DIRECTX, ALLEGRO, MARMALADE,
    };

}

namespace mason::log {}

#endif //MASON_MASON_H
