#include "cel/settings.hpp"

#include "cel/eng/log.hpp"

namespace cel {

    settings_handler::settings_handler(int argc, char **argv) : locale("") {
        std::locale::global(locale);
        
        CEL_IGNORE(argc);
        CEL_IGNORE(argv);
        save_on_destroy = true;

        strs = string_dictionary{this};
    }

    settings_handler::~settings_handler() {
        if (save_on_destroy) save();
    }

    void settings_handler::save() {

    }

    void settings_handler::load() {
        
    }
}