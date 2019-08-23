cmake_minimum_required(VERSION 3.14)
project(Mason VERSION 1.0.0)

set(CMAKE_CXX_STANDARD 17)

add_library(Mason SHARED src/mason.cpp)

target_include_directories(Mason PUBLIC api)
target_include_directories(Mason PUBLIC include)
target_include_directories(Mason PUBLIC src)
target_include_directories(Mason PUBLIC .)
