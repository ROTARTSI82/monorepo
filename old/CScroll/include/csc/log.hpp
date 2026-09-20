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

#include <cstdlib>
#include <exception>

namespace csc {
    enum class LogLevel : uint8_t {
        trace, debug, info, warn, error, critical
    };

    struct LogRecord {
        LogRecord(LogLevel, const char *, unsigned, const char *);

        LogLevel lvl;

        const char *file;
        unsigned line;
        const char *function;
    };

    void _log_impl(const LogRecord &log, std::chrono::system_clock::time_point time, const std::string &msg);

    template <typename... Args>
    void log(const LogRecord &log, const char *fmt_str, Args... format) {
        _log_impl(log, std::chrono::system_clock::now(), fmt::format(fmt_str, format...));
    }
}

#define CEL_IGNORE(val) static_cast<void>(val);

#define CEL_ENABLE_LOG 1

#if CEL_ENABLE_LOG
#   define CEL_TRACE(...) ::csc::log({::csc::LogLevel::trace, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_DEBUG(...) ::csc::log({::csc::LogLevel::debug, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_INFO(...) ::csc::log({::csc::LogLevel::info, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_WARN(...) ::csc::log({::csc::LogLevel::warn, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_ERROR(...) ::csc::log({::csc::LogLevel::error, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#   define CEL_CRITICAL(...) ::csc::log({::csc::LogLevel::critical, __FILE__, __LINE__, __func__}, __VA_ARGS__)
#else
#   define CEL_TRACE(msg, ...)
#   define CEL_DEBUG(msg, ...)
#   define CEL_INFO(msg, ...) 
#   define CEL_WARN(msg, ...) 
#   define CEL_ERROR(msg, ...)
#   define CEL_CRITICAL(msg, ...)
#endif

#endif