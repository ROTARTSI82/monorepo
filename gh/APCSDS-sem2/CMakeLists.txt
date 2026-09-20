cmake_minimum_required(VERSION 3.22)
project(CS)

set(CMAKE_CXX_STANDARD 17)

#add_compile_options(-g -Og -Wall -Wextra -Wpedantic -fno-omit-frame-pointer -fsanitize=address)
#add_link_options(-g -Og -Wall -Wextra -Wpedantic -fno-omit-frame-pointer -fsanitize=address)

add_compile_options(-Ofast)
add_link_options(-Ofast)

add_executable(CS
        Math/TTT/main.cpp)
target_include_directories(CS PUBLIC Math/TTT)