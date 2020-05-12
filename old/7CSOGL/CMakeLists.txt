cmake_minimum_required(VERSION 3.16)
project(CompSciProj)

set(CMAKE_CXX_STANDARD 20)

add_subdirectory(dep/glfw)
add_subdirectory(dep/tinyobjloader)

find_package(GLEW REQUIRED)
find_package(OpenAL REQUIRED)
find_package(OpenGL REQUIRED)

add_executable(CompSciProj src/main.cpp dep/imgui/imgui.cpp dep/imgui/examples/imgui_impl_opengl3.cpp dep/imgui/examples/imgui_impl_glfw.cpp dep/imgui/imgui_demo.cpp dep/imgui/imgui_draw.cpp dep/imgui/imgui_widgets.cpp)
target_include_directories(CompSciProj PUBLIC dep/glfw/include dep/glm dep ${GLEW_INCLUDE_DIRS} ${OPENGL_INCLUDE_DIR} ${OPENAL_INCLUDE_DIR} src dep/imgui)
target_link_libraries(CompSciProj PUBLIC glfw ${GLEW_LIBRARIES} ${OPENGL_LIBRARIES} ${OPENAL_LIBRARY} tinyobjloader)