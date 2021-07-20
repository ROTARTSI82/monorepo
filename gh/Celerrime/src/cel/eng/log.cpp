#include "cel/eng/log.hpp"

#include <ctime>

namespace cel {
    std::string to_string(log_level lvl) {
        switch (lvl) {
        case log_level::trace:
            return "TRACE";
        case log_level::debug:
            return "DEBUG";
        case log_level::info:
            return "INFO";
        case log_level::warn:
            return "WARN";
        case log_level::error:
            return "ERROR";
        case log_level::fatal:
            return "FATAL";
        default:
            return "!!UNKNOWN!!";
        }
    }

    log_record::log_record(log_level le, const char *fi, unsigned li, const char *fu) :
        lvl(le), file(fi), line(li), function(fu) {}

    void _log_impl(const log_record &log, std::chrono::system_clock::time_point time, const std::string &msg) {
        auto ns = std::chrono::duration_cast<std::chrono::nanoseconds>(time - std::chrono::time_point_cast<std::chrono::seconds>(time));    
        std::string final_msg = fmt::format(FMT_STRING("[{} {:%H:%M:%S}.{:0>8}] [{}:{} in {}]: {}"), to_string(log.lvl), time, ns.count(), log.file, log.line, log.function, fmt::to_string(msg));
        std::puts(final_msg.c_str());
    }
}