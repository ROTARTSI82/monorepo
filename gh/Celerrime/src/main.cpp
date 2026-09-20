#include <iostream>

#include "cel/eng/linalg.hpp"
#include "cel/eng/pmr_list.hpp"
#include "cel/app.hpp"
#include "cel/eng/log.hpp"

#include "cel/script/vm.hpp"

int main(int argc, char **argv) {
    std::string dat = cel::read_entire_file("./misc/asm/out.txt");

    cel::scr::vm vm;
    cel::scr::thread t;
    t.parent = &vm;
    t.pc = reinterpret_cast<uint8_t *>(dat.data());
    t.frames.emplace_back(nullptr, 0);

    try {
        while (t.single_cycle());
    } catch (const std::exception &e) {
        CEL_CRITICAL("EXCEPTION: {}", e.what());
    }

    cel::init();
    { // scope is important. quit() should be called after ~app()
        cel::app_t app{argc, argv};
        app.run();
    }
    cel::quit();
}