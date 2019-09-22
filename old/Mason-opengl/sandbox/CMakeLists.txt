cmake_minimum_required(VERSION 3.14.3)

project(Sandbox VERSION 1.0.0 DESCRIPTION "Application run for the Mason library.")

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/bin)

set(CMAKE_CXX_STANDARD 17)

add_executable(Sandbox src/main.cpp)

target_include_directories(Sandbox PUBLIC "${PROJECT_SOURCE_DIR}/../api")
target_include_directories(Sandbox PUBLIC "${PROJECT_SOURCE_DIR}/../include")

target_link_libraries(Sandbox PUBLIC "${PROJECT_SOURCE_DIR}/../build/lib/libMason.a")

set_target_properties(Sandbox PROPERTIES
        ARCHIVE_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        LIBRARY_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        RUNTIME_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/bin"
        )
