cmake_minimum_required(VERSION 3.16)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_EXPORT_COMPILE_COMMANDS 1)

project(Celerrime LANGUAGES CXX)

file(GLOB_RECURSE CEL_SOURCES ${PROJECT_SOURCE_DIR}/src/*.cpp)
add_executable(Celerrime ${CEL_SOURCES})

target_compile_options(Celerrime PRIVATE -Wall -Wextra -Wpedantic -O3)
target_link_options(Celerrime PRIVATE -Wall -Wextra -Wpedantic -O3)

option(USE_ASAN "Compile with address sanitizer" OFF)
option(USE_G3 "Compile with -g3 and -ggdb (debug symbols for gdb)" ON)

if (USE_ASAN)
    target_compile_options(Celerrime PRIVATE -fsanitize=address -fno-omit-frame-pointer)
    target_link_options(Celerrime PRIVATE -fsanitize=address -fno-omit-frame-pointer)
endif()

if (USE_G3)
    target_compile_options(Celerrime PRIVATE -g -g3 -ggdb -fno-omit-frame-pointer)
    target_link_options(Celerrime PRIVATE -g -g3 -ggdb -fno-omit-frame-pointer)
endif()

find_package(GLEW REQUIRED)
find_package(OpenAL REQUIRED)
find_package(OpenGL REQUIRED)


set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)

add_subdirectory(dep/glfw)

find_package(ZLIB REQUIRED)
find_package(Python 3 REQUIRED COMPONENTS Development)

target_link_libraries(Celerrime glfw ZLIB::ZLIB)
target_link_libraries(Celerrime OpenGL::GL GLEW::GLEW ${OPENAL_LIBRARY})
target_link_libraries(Celerrime Python::Module Python::Python)

add_subdirectory(dep/fmt)
target_link_libraries(Celerrime fmt::fmt)

target_include_directories(Celerrime PRIVATE ${OPENGL_INCLUDE_DIR} ${OPENAL_INCLUDE_DIR} dep dep/imgui)
target_include_directories(Celerrime PUBLIC include src)
target_precompile_headers(Celerrime PUBLIC <GL/glew.h> <GLFW/glfw3.h> <fmt/format.h> <fmt/chrono.h> <cel/constants.hpp> <cel/eng/log.hpp> <cel/eng/misc_util.hpp> <cel/eng/pmr_list.hpp>)
