/**
 * @file log.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-19
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_LOG_HPP
#define CEL_ENG_LOG_HPP

#include <string>
#include <cstdint>
#include <chrono>

#include <fmt/format.h>
#include <fmt/chrono.h>

#include "cel/constants.hpp"

namespace cel {
    enum class log_level : uint8_t {
        trace, debug, info, warn, error, fatal
    };

    std::string to_string(log_level lvl);

    struct log_record {

        log_record(log_level, const char *, unsigned, const char *);

        log_level lvl;

        const char *file;
        unsigned line;
        const char *function;
    };

    void _log_impl(const log_record &log, std::chrono::system_clock::time_point time, const std::string &msg);

    template <typename... Args>
    void log(const log_record &log, const char *fmt_str, Args... format) {
        _log_impl(log, std::chrono::system_clock::now(), fmt::format(fmt_str, format...));
    }
}

#if CEL_ENABLE_LOG
#   define TRACE(...) ::cel::log({::cel::log_level::trace, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define DEBUG(...) ::cel::log({::cel::log_level::debug, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define INFO(...) ::cel::log({::cel::log_level::info, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define WARN(...) ::cel::log({::cel::log_level::warn, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define ERROR(...) ::cel::log({::cel::log_level::error, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define FATAL(...) ::cel::log({::cel::log_level::fatal, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#else
#   define TRACE(msg, ...)
#   define DEBUG(msg, ...)
#   define INFO(msg, ...) 
#   define WARN(msg, ...) 
#   define ERROR(msg, ...)
#   define FATAL(msg, ...)
#endif

#endif