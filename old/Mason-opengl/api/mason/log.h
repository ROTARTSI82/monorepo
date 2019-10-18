//
// Created by Grant on 9/26/19.
//

#pragma once

#ifndef MASONSHARE_LOG_H
#define MASONSHARE_LOG_H

#include <sstream>
#include <iomanip>
#include <iostream>

#define SPDLOG_ACTIVE_LEVEL SPDLOG_LEVEL_TRACE

#include "mason/mason.h"

#ifdef MASON_ENABLE_LOGGING

#include "spdlog/spdlog.h"
#include "spdlog/sinks/stdout_color_sinks.h"
#include "spdlog/sinks/basic_file_sink.h"

#define MASON_TRACE SPDLOG_TRACE
#define MASON_DEBUG SPDLOG_DEBUG
#define MASON_INFO SPDLOG_INFO
#define MASON_WARN SPDLOG_WARN
#define MASON_CRITICAL SPDLOG_CRITICAL
#define MASON_FATAL SPDLOG_CRITICAL

#else

#define MASON_TRACE(...)
#define MASON_DEBUG(...)
#define MASON_INFO(...)
#define MASON_WARN(...)
#define MASON_CRITICAL(...)
#define MASON_FATAL(...)

#endif

namespace mason {
    void init_logging(bool use_single_file);
}

#endif // MASONSHARE_LOG_H
