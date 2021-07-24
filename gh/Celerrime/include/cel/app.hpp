/**
 * @file app.hpp
 * @brief 
 * @date 2021-07-11
 */

#pragma once

#ifndef CEL_APP_HPP
#define CEL_APP_HPP

#include "cel/eng/window.hpp"
#include "cel/settings.hpp"

namespace cel {
    void init();
    void quit();
    class app {
    private:
        settings_handler settings;
        window ctl_panel;

    public:
        app(int argc, char **argv);
        ~app();

        // finer control in the future?
        void run();
    };
}

#endif
