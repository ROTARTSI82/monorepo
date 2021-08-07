#include "cel/eng/log.hpp"

#include <ctime>

namespace cel {
    static std::pair<std::string, std::string> to_string(log_level lvl) {
        switch (lvl) {
        case log_level::trace:
            return std::make_pair("\033[97m", "TRACE");
        case log_level::debug:
            return std::make_pair("\033[96m", "DEBUG");
        case log_level::info:
            return std::make_pair("\033[92m", "INFO");
        case log_level::warn:
            return std::make_pair("\033[93m", "WARN");
        case log_level::error:
            return std::make_pair("\033[91m", "ERROR");
        case log_level::critical:
            return std::make_pair("\033[94m", "CRITICAL");
        default:
            return std::make_pair("", "UNKNOWN"); // freak out.
        }
    }

    log_record::log_record(log_level le, const char *fi, unsigned li, const char *fu) :
        lvl(le), file(fi), line(li), function(fu) {}

    void _log_impl(const log_record &log, std::chrono::system_clock::time_point time, const std::string &msg) {
        auto ns = std::chrono::duration_cast<std::chrono::nanoseconds>(time - std::chrono::time_point_cast<std::chrono::seconds>(time));

        auto [lvl_fmt, lvl_name] = to_string(log.lvl);

        std::string final_msg = fmt::format(FMT_STRING("[{:>32}:{:<4} {:<16}] [{:%H:%M:%S}.{:09} {}{:<8}\033[0m]: \033[1m{}\033[0m"), log.file, log.line, log.function, time, ns.count(), lvl_fmt, lvl_name, fmt::to_string(msg));
        std::puts(final_msg.c_str());
    }
}