cmake_minimum_required(VERSION 3.16)
project(GameTest C)

set(CMAKE_C_STANDARD 99)

add_executable(GameTest src/main.c src/linalg.c src/gl.c src/common.c src/file_reader.c src/phys.c src/util.c)
target_compile_options(GameTest PRIVATE -O0 -g3 -Wall -Wextra -Wpedantic -fsanitize=address -fno-omit-frame-pointer)
target_link_options(GameTest PRIVATE -O0 -g3 -Wall -Wextra -Wpedantic -fsanitize=address -fno-omit-frame-pointer)

find_package(GLEW REQUIRED)
find_package(OpenAL REQUIRED)
find_package(OpenGL REQUIRED)

# This might only work on UNIX
#find_package(PkgConfig)
#if (PKG_CONFIG_FOUND)
#    pkg_search_module(GLFW REQUIRED glfw3)
#    target_include_directories(GameTest PRIVATE ${GLFW_INCLUDE_DIRS})
#    target_link_libraries(GameTest ${GLFW_LIBRARIES})
#else()
#    set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
#    set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
#    set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
#
#    add_subdirectory(dep/glfw)
#    target_link_libraries(GameTest glfw)
#endif()

find_package(glfw3 REQUIRED)
find_package(ZLIB REQUIRED)
target_link_libraries(GameTest glfw ZLIB::ZLIB)


target_link_libraries(GameTest OpenGL::GL GLEW::GLEW ${OPENAL_LIBRARY} m) # m being libmath
target_include_directories(GameTest PRIVATE ${OPENGL_INCLUDE_DIR} include src ${OPENAL_INCLUDE_DIR})