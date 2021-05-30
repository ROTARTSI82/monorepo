cmake_minimum_required(VERSION 3.19)
project(GameTest C)

set(CMAKE_C_STANDARD 99)

add_executable(GameTest src/main.c src/linalg.c src/gl.c)

find_package(GLEW REQUIRED)
find_package(OpenAL REQUIRED)
find_package(OpenGL REQUIRED)

# This might only work on UNIX
find_package(PkgConfig REQUIRED)
pkg_search_module(GLFW REQUIRED glfw3)
target_include_directories(GameTest PRIVATE ${GLFW_INCLUDE_DIRS})
target_link_libraries(GameTest ${GLFW_LIBRARIES})

target_link_libraries(GameTest OpenGL::GL GLEW::GLEW ${OPENAL_LIBRARY} m) # m being libmath
target_include_directories(GameTest PRIVATE ${OPENGL_INCLUDE_DIR} ${GLEW_INCLUDE_DIRS} include src ${OPENAL_INCLUDE_DIR})