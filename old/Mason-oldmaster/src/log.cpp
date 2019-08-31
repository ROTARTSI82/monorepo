//
// Created by Grant on 2019-08-24.
//

#include "mason/log.h"

//struct stat info;

#ifdef MASON_DEBUG_MODE
namespace mason::log {
    void init(bool singleFileMode) {
        try {
            std::string patternStr = "%^[%Y.%m.%d] [%H:%M:%S.%F] [%n:%l] [%s:%#]: %v%$";

            // Sink for outputting to the console
            auto console_sink = std::make_shared<spdlog::sinks::stdout_color_sink_mt>();

            // Get the datetime
            std::ostringstream oss;
            auto t = std::time(nullptr);
            auto tm = *std::localtime(&t);
            oss << std::put_time(&tm, "%Y.%m.%d %H:%M:%S");

            // Sink for outputting to file
            std::shared_ptr<spdlog::sinks::basic_file_sink_mt> file_sink;
            if (!singleFileMode) {
                file_sink = std::make_shared<spdlog::sinks::basic_file_sink_mt>("logs/" + oss.str() + ".log", true);
            } else {
                file_sink = std::make_shared<spdlog::sinks::basic_file_sink_mt>("latest.log", true);
            }

            // Create a logger using both sinks, set it as the default logger, and register it.
            std::shared_ptr<spdlog::logger> logger = std::make_shared<spdlog::logger>("root",
                                                                                      spdlog::sinks_init_list(
                                                                                              {console_sink,
                                                                                               file_sink}));

            spdlog::register_logger(logger);
            spdlog::set_default_logger(logger);

            spdlog::set_level(spdlog::level::trace);
            spdlog::set_pattern("%v");

                    MASON_INFO(
                    "================================[NEW LOGGING SESSION | {}]================================",
                    oss.str())

            spdlog::set_pattern(patternStr);

            spdlog::set_error_handler([](const std::string &msg) {
                std::cerr << "[** LOG ERROR **]: " << msg << std::endl;
                MASON_WARN("[** SPDLOG ERROR **]: {}", msg)
            });
        }
        catch (const spdlog::spdlog_ex &ex) {
            std::cerr << "Log initialization failed: " << ex.what() << std::endl;
        }
    }
}
#endif

#ifndef MASON_DEBUG_MODE

namespace mason::log {
    void init(bool singleFileMode) {}
}

#endif
