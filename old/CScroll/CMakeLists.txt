cmake_minimum_required(VERSION 3.2)

add_compile_options(-fsanitize=address -fno-omit-frame-pointer -std=c++17 -Og -g3 -ggdb)
add_link_options(-fsanitize=address -Og -g3 -ggdb)

find_program(CCACHE_FOUND ccache)
if (CCACHE_FOUND)
    message("-- Ccache enabled")
    set_property(GLOBAL PROPERTY RULE_LAUNCH_COMPILE ccache)
    set_property(GLOBAL PROPERTY RULE_LAUNCH_LINK ccache)
endif(CCACHE_FOUND)

project(cscroll)
project(csc)

set(CMAKE_CXX_STANDARD 17)

add_subdirectory(dep/fmt)

find_package(ZLIB REQUIRED)

file(GLOB_RECURSE CSC_SOURCES ${PROJECT_SOURCE_DIR}/src/csc/*.cpp)
message("Sources = ${CSC_SOURCES}")
add_library(csc STATIC ${CSC_SOURCES})
add_executable(cscroll src/main.cpp)

target_link_libraries(cscroll csc)
target_link_libraries(csc PUBLIC fmt ZLIB::ZLIB)

target_include_directories(cscroll PRIVATE src)
target_include_directories(cscroll PUBLIC include)
target_include_directories(csc PRIVATE src)
target_include_directories(csc PUBLIC include)
