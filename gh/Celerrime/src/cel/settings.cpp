#include "cel/settings.hpp"

#include "cel/eng/log.hpp"
#include "cel/eng/load_ini.hpp"
namespace cel {

    settings_handler::settings_handler(int argc, char **argv) : locale("") {
        std::locale::global(locale);
        
        CEL_IGNORE(argc);
        CEL_IGNORE(argv);
        save_on_destroy = true;

        try {
            load_ini(res_path("lang/en.ini"), strs);
        } catch (const std::exception &e) {
            CEL_ERROR("Failed to load strings from lang/en_US.ini: {}", e.what());
        }

        for (const auto &p : strs) {
            CEL_INFO("{}={}", p.first, p.second);
        }
    }

    settings_handler::~settings_handler() {
        if (save_on_destroy) save();
    }

    std::string settings_handler::txt(const std::string &key) {
        if (strs.count(key)) return strs[key];
        CEL_WARN("Missing translation key: {}", key);
        return key;
    }

    std::string settings_handler::res_path(const std::string &file, bool essential) {
        (void) essential;
        return "./res/" + file;
    }

    void settings_handler::save() {

    }

    void settings_handler::load() {
        
    }
}