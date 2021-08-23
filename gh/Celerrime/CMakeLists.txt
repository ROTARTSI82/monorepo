cmake_minimum_required(VERSION 3.16)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_EXPORT_COMPILE_COMMANDS 1)

add_compile_options(-Og)
add_link_options(-Og)

set(BUILD_SHARED_LIBS OFF CACHE BOOL "")
set(GLFW_BUILD_DOCS OFF CACHE BOOL "")
set(GLFW_BUILD_TESTS OFF CACHE BOOL "")
set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "")

set(GLEW_USE_STATIC_LIBS ON CACHE BOOL "")

option(USE_ASAN "Compile with address sanitizer" OFF)
option(USE_G3 "Compile with -g3 and -ggdb (debug symbols for gdb)" ON)

if (USE_G3)
    add_compile_options(-g -g3 -ggdb -fno-omit-frame-pointer)
    add_link_options(-g -g3 -ggdb -fno-omit-frame-pointer)
endif()

if (USE_ASAN)
    add_compile_options(-fsanitize=address -fno-omit-frame-pointer)
    add_link_options(-fsanitize=address -fno-omit-frame-pointer)
endif()


find_program(CCACHE_FOUND ccache)
if (CCACHE_FOUND)
    message("-- Ccache enabled")
    set_property(GLOBAL PROPERTY RULE_LAUNCH_COMPILE ccache)
    set_property(GLOBAL PROPERTY RULE_LAUNCH_LINK ccache)
endif(CCACHE_FOUND)

project(cel LANGUAGES CXX)

file(GLOB_RECURSE CEL_SOURCES ${PROJECT_SOURCE_DIR}/src/cel/*.cpp)

set(CEL_IMGUI_SOURCES dep/imgui/imgui.cpp dep/imgui/imgui_demo.cpp dep/imgui/imgui_draw.cpp dep/imgui/imgui_tables.cpp dep/imgui/imgui_widgets.cpp)
set(CEL_IMGUI_BACKENDS dep/imgui/backends/imgui_impl_glfw.cpp dep/imgui/backends/imgui_impl_opengl3.cpp)
add_library(cel STATIC ${CEL_SOURCES} ${CEL_IMGUI_SOURCES} ${CEL_IMGUI_BACKENDS})

target_compile_options(cel PRIVATE -Wall -Wextra -Wpedantic)
target_link_options(cel PRIVATE -Wall -Wextra -Wpedantic)

find_package(GLEW REQUIRED)
find_package(OpenGL REQUIRED)
find_package(Threads REQUIRED)

add_subdirectory(dep/glfw)

# find_package(ZLIB REQUIRED)
# target_link_libraries(Celerrime ZLIB::ZLIB)

# find_package(Python 3 REQUIRED COMPONENTS Development)
# target_link_libraries(Celerrime Python::Module Python::Python)

# find_package(OpenAL REQUIRED)
# target_link_libraries(Celerrime ${OPENAL_LIBRARY})
# target_include_directories(Celerrime PRIVATE ${OPENAL_INCLUDE_DIR})

target_link_libraries(cel glfw Threads::Threads)
target_link_libraries(cel OpenGL::GL GLEW::GLEW)

add_subdirectory(dep/fmt)
target_link_libraries(cel fmt::fmt)

target_include_directories(cel PRIVATE ${OPENGL_INCLUDE_DIR} src dep dep/imgui)
target_include_directories(cel PUBLIC include)
# target_precompile_headers(cel PUBLIC <GL/glew.h> <GLFW/glfw3.h> <fmt/format.h> <fmt/chrono.h> <cel/constants.hpp> <cel/eng/log.hpp> <cel/eng/misc_util.hpp> <cel/eng/pmr_list.hpp>)

project(celerrime LANGUAGES CXX)
add_executable(celerrime src/main.cpp)
target_link_libraries(celerrime PUBLIC cel)
