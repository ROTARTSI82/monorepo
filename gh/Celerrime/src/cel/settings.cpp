#include "cel/settings.hpp"

namespace cel {

    settings_handler::settings_handler() {
        save_on_destroy = true;
    }

    settings_handler::~settings_handler() {
        if (save_on_destroy) save();
    }

    void settings_handler::save() {

    }
}