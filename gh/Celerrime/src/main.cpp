#include <iostream>

#include "cel/eng/linalg.hpp"
#include "cel/eng/pmr_list.hpp"
#include "cel/app.hpp"
#include "cel/eng/log.hpp"

int main(int argc, char **argv) {
    cel::init();
    { // scope is important. quit() should be called after ~app()
        cel::app_t app{argc, argv};
        app.run();
    }
    cel::quit();
}