//
// Created by Grant on 9/26/19.
//
#include "mason/log.h"

#ifdef MASON_ENABLE_LOGGING
namespace mason {
    static void on_spdlog_err(const std::string &msg) {
        std::cerr << "[** SPDLOG ERROR **]: " << msg << std::endl;
        MASON_CRITICAL("[** SPDLOG ERROR **]: {}", msg);
    }

    void init_logging(bool use_single_file) {
        try {
            std::string pattern_str = "%^[%Y.%m.%d] [%H:%M:%S.%F] [%n:%l] [%s:%#]: %v%$";

            // Sink for outputting to the console
            auto console_sink = std::make_shared<spdlog::sinks::stdout_color_sink_mt>();

            // Get the datetime
            std::ostringstream oss;
            auto t = std::time(nullptr);
            auto tm = *std::localtime(&t);
            oss << std::put_time(&tm, "%Y.%m.%d %H:%M:%S");

            // Sink for outputting to file
            std::shared_ptr<spdlog::sinks::basic_file_sink_mt> file_sink;
            if (!use_single_file) {
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
                    oss.str());

            spdlog::set_pattern(pattern_str);

            spdlog::set_error_handler(on_spdlog_err);

            MASON_INFO("Mason logging successfully started! Using {}", MASON_VERSION_STRING);
        }
        catch (const spdlog::spdlog_ex &ex) {
            std::cerr << "Log initialization failed: " << ex.what() << std::endl;
            MASON_CRITICAL("Log initialization failed: {}", ex.what());
        }
    }
}
#else
namespace mason {
    void init_logging(bool use_single_file) {}
}
#endif
