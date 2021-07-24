#include <iostream>

#include "cel/eng/linalg.hpp"
#include "cel/eng/pmr_list.hpp"
#include <forward_list>
#include "cel/app.hpp"
#include "cel/eng/log.hpp"

int main(int argc, char **argv) {
    cel::mat4 persp = cel::mat4::perspective(60, 4.0 / 3, 0.1, 100);
    cel::pmr_list<int> test;
    test.push_front(8);
    test.push_front(12);
    test.insert_after(test.begin(), {1, 2, 3});

    for (const int *i : test) {
        std::cout << *i << std::endl;
    }


    for (int i = 0; i < 256; i++)
        CEL_INFO("TESTING 123");


    std::cout << "test: " << persp.c2.w << std::endl;
    std::cout << "pi = " << cel::consts_for<float>::calc().pi << std::endl;
    std::cout << "rt2 = " << cel::consts_for<float>::calc().root2 << std::endl;
    
    cel::init();
    cel::app app{argc, argv};
    app.run();
    cel::quit();
}