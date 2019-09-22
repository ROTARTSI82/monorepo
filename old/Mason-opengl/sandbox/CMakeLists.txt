cmake_minimum_required(VERSION 3.14.3)

project(Sandbox VERSION 1.0.0 DESCRIPTION "Application run for the Mason library.")

set(CMAKE_CXX_STANDARD 17)

add_executable(Sandbox src/main.cpp)

target_include_directories(Sandbox PUBLIC "${PROJECT_SOURCE_DIR}/../api")
target_include_directories(Sandbox PUBLIC "${PROJECT_SOURCE_DIR}/../include")

target_link_libraries(Sandbox PUBLIC "${PROJECT_SOURCE_DIR}/../build/lib/libMason.a")
