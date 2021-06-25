cmake_minimum_required(VERSION 3.16)
project(Celerrime C)

SET(CMAKE_EXPORT_COMPILE_COMMANDS 1)
set(CMAKE_C_STANDARD 99)

message("CXX Compiler = ${CMAKE_CXX_COMPILER} C Compiler = ${CMAKE_C_COMPILER}")

add_executable(Celerrime src/main.c src/linalg.c src/gl.c src/common.c src/file_reader.c src/phys.c src/util.c src/main_helper.c
                         src/scripting/pymodule.c)

set(WARNING_FLAGS -Wall -Wextra -Wpedantic -Wabi -Wbad-function-cast -Wcast-qual -Wfloat-equal -Wformat=2
-Wformat-nonliteral -Wformat-security -Winit-self -Winvalid-pch -Wmissing-declarations -Wmissing-include-dirs  -Wstrict-overflow=5
-Wmultichar -Wnested-externs -Wold-style-definition -Wredundant-decls -Wformat-y2k -Wstrict-aliasing
-Wmissing-noreturn -Wc++-compat -Walloca -Wdate-time -Wshift-overflow -Wunused-const-variable
-Wmissing-format-attribute -Wswitch-default -Wswitch-enum -Wundef -Wunused-macros -Wwrite-strings)
# disabled:
# -Wdouble-promotion (Warns when a float is implicity converted to a double)
# -Wfloat-conversion is implicitly enabled by -Wconversion
# -Wtraditional -Wtraditional-conversion
# -Wunsuffixed-float-constants
# -Wsystem-headers
# -Wmissing-prototypes 
# -Wsign-conversion enabled by -Wconversion
# -Wstrict-prototypes warns about func() because it wants func(void) and it's stupid
# -Walloc-size-larger-than= and -Walloca-larger-than=
# -Wconversion warns about stupid stuff.


target_compile_options(Celerrime PRIVATE ${WARNING_FLAGS})
target_link_options(Celerrime PRIVATE ${WARNING_FLAGS})

option(USE_ASAN "Compile with google address sanitizer" ON)
option(USE_O3 "Compile with -O3 (maximum optimization)" ON)
option(USE_G3 "Compile with -g3 and -ggdb (debug symbols for gdb)" ON)

if (USE_ASAN)
    target_compile_options(Celerrime PRIVATE -fsanitize=address -fno-omit-frame-pointer)
    target_link_options(Celerrime PRIVATE -fsanitize=address -fno-omit-frame-pointer)
endif()

if (USE_O3)
    # -Ofast is prolly bad
    target_compile_options(Celerrime PRIVATE -O3)
    target_link_options(Celerrime PRIVATE -O3)
endif()

if (USE_G3)
    target_compile_options(Celerrime PRIVATE -g -g3 -ggdb -fno-omit-frame-pointer)
    target_link_options(Celerrime PRIVATE -g -g3 -ggdb -fno-omit-frame-pointer)
endif()

find_package(GLEW REQUIRED)
find_package(OpenAL REQUIRED)
find_package(OpenGL REQUIRED)

# This might only work on UNIX
#find_package(PkgConfig)
#if (PKG_CONFIG_FOUND)
#    pkg_search_module(GLFW REQUIRED glfw3)
#    target_include_directories(Celerrime PRIVATE ${GLFW_INCLUDE_DIRS})
#    target_link_libraries(Celerrime ${GLFW_LIBRARIES})
#else()
#    set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
#    set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
#    set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
#
#    add_subdirectory(dep/glfw)
#    target_link_libraries(Celerrime glfw)
#endif()

find_package(glfw3 REQUIRED)
find_package(ZLIB REQUIRED)
find_package(Python 3 REQUIRED COMPONENTS Development)
target_link_libraries(Celerrime glfw ZLIB::ZLIB)

target_link_libraries(Celerrime OpenGL::GL GLEW::GLEW ${OPENAL_LIBRARY} m pthread) # m being libmath
target_link_libraries(Celerrime Python::Module Python::Python)

target_include_directories(Celerrime PRIVATE ${OPENGL_INCLUDE_DIR} include src ${OPENAL_INCLUDE_DIR})