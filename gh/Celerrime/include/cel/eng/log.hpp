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
#include <cstdlib>
#include <exception>

namespace cel {
    enum class log_level : uint8_t {
        trace, debug, info, warn, error, critical
    };

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

#define CEL_IGNORE(val) static_cast<void>(val);

#if CEL_ENABLE_LOG
#   define CEL_TRACE(...) ::cel::log({::cel::log_level::trace, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_DEBUG(...) ::cel::log({::cel::log_level::debug, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_INFO(...) ::cel::log({::cel::log_level::info, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_WARN(...) ::cel::log({::cel::log_level::warn, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_ERROR(...) ::cel::log({::cel::log_level::error, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_CRITICAL(...) ::cel::log({::cel::log_level::critical, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#else
#   define CEL_TRACE(msg, ...)
#   define CEL_DEBUG(msg, ...)
#   define CEL_INFO(msg, ...) 
#   define CEL_WARN(msg, ...) 
#   define CEL_ERROR(msg, ...)
#   define CEL_CRITICAL(msg, ...)
#endif

#define CEL_FIRST_ARG(N, ...) N
#define CEL_THROWIF(condition, ...) if (condition) { CEL_CRITICAL(__VA_ARGS__); throw std::runtime_error{CEL_FIRST_ARG(__VA_ARGS__)}; }


#endif