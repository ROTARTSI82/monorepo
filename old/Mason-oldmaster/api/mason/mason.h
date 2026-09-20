#pragma once

#ifndef MASON_MASON_H
#define MASON_MASON_H

#define MASON_DEBUG_MODE

#include <iostream>
#include <ctime>
#include <iomanip>
#include <sstream>
#include <unordered_map>
#include <thread>
#include <memory>
#include <vector>
#include <queue>
#include <fstream>


#define SPDLOG_ACTIVE_LEVEL SPDLOG_LEVEL_TRACE

#include "spdlog/spdlog.h"
#include "spdlog/sinks/stdout_color_sinks.h"
#include "spdlog/sinks/basic_file_sink.h"

#include "mason/log.h"

namespace mason {
    extern bool debugModeActive;

    enum API {
        OPENGL, AUTO_BY_PLATFORM, VULKAN, METAL, DIRECTX, ALLEGRO, MARMALADE,
    };

}

namespace mason::log {}

#endif //MASON_MASON_H
