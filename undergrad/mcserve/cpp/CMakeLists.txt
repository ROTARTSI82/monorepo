cmake_minimum_required(VERSION 3.31)
project(cpp)

set(CMAKE_CXX_STANDARD 26)

add_executable(cpp main.cpp)
target_include_directories(cpp PUBLIC ./)
target_compile_options(cpp PUBLIC -fsanitize=address -fno-omit-frame-pointer)
target_link_options(cpp PUBLIC -fsanitize=address -fno-omit-frame-pointer)
