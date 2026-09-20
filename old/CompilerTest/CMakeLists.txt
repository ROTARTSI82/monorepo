cmake_minimum_required(VERSION 3.16)
project(CompilerTest)

add_compile_options(-ggdb -g3 -std=c++17)

file(GLOB_RECURSE SRC src/*.cpp)
add_executable(CompilerTest ${SRC})

target_include_directories(CompilerTest PUBLIC include src)

