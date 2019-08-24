cmake_minimum_required(VERSION 3.9)
project(Mason VERSION 1.0.0 DESCRIPTION "Mason description")

set(CMAKE_CXX_STANDARD 17)

add_library(Mason src/mason.cpp src/application.cpp src/thread.cpp src/log.cpp)

set_target_properties(Mason PROPERTIES
        VERSION ${PROJECT_VERSION}
        SOVERSION 1.0.0
        PUBLIC_HEADER api/mason/mason.h
        ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
        )

configure_file(build/mason.pc.in mason.pc)

target_include_directories(Mason PUBLIC src)
target_include_directories(Mason PRIVATE api)
target_include_directories(Mason PRIVATE include)
target_include_directories(Mason PUBLIC .)
