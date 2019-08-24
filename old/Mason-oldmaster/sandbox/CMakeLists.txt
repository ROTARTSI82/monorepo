cmake_minimum_required(VERSION 3.14)
project(Sandbox VERSION 1.0.0)

set(CMAKE_CXX_STANDARD 17)

add_executable(Sandbox src/sandbox.cpp)

target_include_directories(Sandbox PRIVATE ../api)
target_include_directories(Sandbox PRIVATE ../include)
target_include_directories(Sandbox PRIVATE ../src)
target_include_directories(Sandbox PRIVATE ..)

target_link_libraries(Sandbox ${PROJECT_SOURCE_DIR}/../build/lib/libMason.a)
