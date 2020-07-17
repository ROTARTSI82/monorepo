cmake_minimum_required(VERSION 3.13.5) # I don't know if this is right. But hey, it works!

SET(CMAKE_CXX_STANDARD 17)

set(TML_VERSION 0.0.0)

add_compile_options(-O3 -Wall -g3 -pg -ggdb -Wextra -std=c++17 -fsanitize=address -Wpedantic -Wshadow)

project(LibTML_STATIC LANGUAGES CXX VERSION ${TML_VERSION} DESCRIPTION "The Turing Machine Language Library")
add_library(LibTML_STATIC STATIC src/tml/turing_machine.cpp)
target_link_libraries(LibTML_STATIC asan)
target_include_directories(LibTML_STATIC PUBLIC src include)
set_target_properties("LibTML_STATIC" PROPERTIES OUTPUT_NAME "TML")

project(LibTML_SHARED LANGUAGES CXX VERSION ${TML_VERSION} DESCRIPTION "The Turing Machine Language Library")
add_library(LibTML_SHARED SHARED src/tml/turing_machine.cpp)
target_link_libraries(LibTML_SHARED asan)
target_include_directories(LibTML_SHARED PUBLIC src include)
set_target_properties("LibTML_SHARED" PROPERTIES OUTPUT_NAME "TML")


project(TML LANGUAGES CXX VERSION ${TML_VERSION} DESCRIPTION "TML interpreter and debugger")
add_executable(TML apps/tml.cpp)
target_include_directories(TML PUBLIC include apps)
target_link_libraries(TML PUBLIC LibTML_STATIC asan)
set_target_properties("TML" PROPERTIES OUTPUT_NAME "tml")
