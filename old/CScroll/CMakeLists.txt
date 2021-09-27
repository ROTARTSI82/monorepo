cmake_minimum_required(VERSION 3.2)

add_compile_options(-std=c++17 -Og -g3 -ggdb -fno-omit-frame-pointer)


project(cscroll)
project(csc)

set(CMAKE_CXX_STANDARD 17)

add_subdirectory(dep/fmt)

file(GLOB_RECURSE CSC_SOURCES ${PROJECT_SOURCE_DIR}/src/csc/*.cpp)
add_library(csc STATIC ${CSC_SOURCES})
add_executable(cscroll src/main.cpp)

target_link_libraries(cscroll PRIVATE csc)
target_link_libraries(csc PUBLIC fmt)

target_include_directories(cscroll PRIVATE src)
target_include_directories(cscroll PUBLIC include)
target_include_directories(csc PRIVATE src)
target_include_directories(csc PUBLIC include)
