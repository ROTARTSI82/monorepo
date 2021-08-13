#include <iostream>

#include "cel/eng/linalg.hpp"
#include "cel/eng/pmr_list.hpp"
#include <forward_list>
#include "cel/app.hpp"
#include "cel/eng/log.hpp"

int main(int argc, char **argv) {

    // glm::vec4 test{1, 1, 1, 1};
    // glm::vec2 v2test{1, 1};
    // auto start = std::chrono::high_resolution_clock::now();
    // for (unsigned long i = 0; i < 4096 * 32; i++) {
    //     test = cel::mat4_transform(glm::vec3{(float) i, (float) i + 2, (float) i + 1} * (i % 2 ? 1.0f : -1.0f), {1, 1}, 0.5) * test;
    //     v2test = glm::normalize(cel::mat2_transform({i, i}, i * 2) * v2test);
    //     glm::length(v2test);
    //     // s = glm::dot(v2test, {3.1415, 420.69});
    // }
    // auto end = std::chrono::high_resolution_clock::now();
    // CEL_INFO("GLM Finished in: {}\n result: {}", end - start, glm::to_string(test));

    cel::init();
    { // scope is important. quit() should be called after ~app()
        cel::app app{argc, argv};
        app.run();
    }
    cel::quit();
}