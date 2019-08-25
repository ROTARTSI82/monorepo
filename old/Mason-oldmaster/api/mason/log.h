//
// Created by Grant on 2019-08-24.
//

#pragma once

#ifndef MASON_LOG_H
#define MASON_LOG_H

#include "mason/masonpch.h"
#include "mason/mason.h"

#ifndef MASON_DEBUG_MODE
#define log_trace(...)
#define log_debug(...)
#define log_info(...)
#define log_warn(...)
#define log_critical(...)
#define log_fatal(...)
#endif // !MASON_DEBUG_MODE

#ifdef MASON_DEBUG_MODE
#define log_trace(...) SPDLOG_LOGGER_TRACE(spdlog::default_logger_raw(), __VA_ARGS__)
#define log_debug(...) SPDLOG_LOGGER_DEBUG(spdlog::default_logger_raw(), __VA_ARGS__)
#define log_info(...)  SPDLOG_LOGGER_INFO(spdlog::default_logger_raw(), __VA_ARGS__)
#define log_warn(...)  SPDLOG_LOGGER_WARN(spdlog::default_logger_raw(), __VA_ARGS__)
#define log_critical(...) SPDLOG_LOGGER_CRITICAL(spdlog::default_logger_raw(), __VA_ARGS__)
#define log_fatal(...) SPDLOG_LOGGER_CRITICAL(spdlog::default_logger_raw(), __VA_ARGS__)
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
