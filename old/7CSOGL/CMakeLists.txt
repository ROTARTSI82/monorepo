cmake_minimum_required(VERSION 3.16)
project(CompSciProj)

set(CMAKE_CXX_STANDARD 20)

add_subdirectory(dep/glfw)
find_package(GLEW REQUIRED)

add_executable(CompSciProj main.cpp)
target_include_directories(CompSciProj PUBLIC dep/glfw/include dep/glm ${GLEW_INCLUDE_DIRS})
target_link_libraries(CompSciProj PUBLIC glfw ${GLEW_LIBRARIES})