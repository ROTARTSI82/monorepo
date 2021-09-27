#include "csc/log.hpp"

#include <ctime>

namespace csc {
    static std::pair<std::string, std::string> to_string(LogLevel lvl) {
        switch (lvl) {
        case LogLevel::trace:
            return std::make_pair("\033[97m", "TRACE");
        case LogLevel::debug:
            return std::make_pair("\033[96m", "DEBUG");
        case LogLevel::info:
            return std::make_pair("\033[92m", "INFO");
        case LogLevel::warn:
            return std::make_pair("\033[93m", "WARN");
        case LogLevel::error:
            return std::make_pair("\033[91m", "ERROR");
        case LogLevel::critical:
            return std::make_pair("\033[94m", "CRITICAL");
        default:
            return std::make_pair("", "UNKNOWN"); // freak out.
        }
    }

    LogRecord::LogRecord(LogLevel le, const char *fi, unsigned li, const char *fu) :
        lvl(le), file(fi), line(li), function(fu) {}

    void _log_impl(const LogRecord &log, std::chrono::system_clock::time_point time, const std::string &msg) {
        auto ns = std::chrono::duration_cast<std::chrono::nanoseconds>(time - std::chrono::time_point_cast<std::chrono::seconds>(time));

        auto [lvl_fmt, lvl_name] = to_string(log.lvl);

        std::string final_msg = fmt::format(FMT_STRING("[{:>32}:{:<4} {:<16}] [{:%H:%M:%S}.{:09} {}{:<8}\033[0m]: \033[1m{}\033[0m"), log.file, log.line, log.function, time, ns.count(), lvl_fmt, lvl_name, fmt::to_string(msg));
        std::puts(final_msg.c_str());
    }
}