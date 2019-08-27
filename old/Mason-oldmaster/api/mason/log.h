//
// Created by Grant on 2019-08-24.
//

#pragma once

#ifndef MASON_LOG_H
#define MASON_LOG_H

#include "mason/masonpch.h"
#include "mason/mason.h"

#ifndef MASON_DEBUG_MODE
#define MASON_TRACE(...)
#define MASON_DEBUG(...)
#define MASON_INFO(...)
#define MASON_WARN(...)
#define MASON_CRITICAL(...)
#define MASON_FATAL(...)
#endif // !MASON_DEBUG_MODE

#ifdef MASON_DEBUG_MODE
#define MASON_TRACE SPDLOG_TRACE
#define MASON_DEBUG SPDLOG_DEBUG
#define MASON_INFO SPDLOG_INFO
#define MASON_WARN SPDLOG_WARN
#define MASON_CRITICAL SPDLOG_CRITICAL
#define MASON_FATAL SPDLOG_CRITICAL
#endif // MASON_DEBUG_MODE

#define FG_RED "\033[31m"
#define FG_GREEN "\033[32m"
#define FG_BLUE "\033[34m"
#define FG_RESET "\033[39m"

#define BG_RED "\033[41m"
#define BG_GREEN "\033[42m"
#define BG_BLUE "\033[44m"
#define BG_RESET "\033[49m"

namespace mason::log {
    void init(bool singleFileMode);
}

#endif //MASON_LOG_H
