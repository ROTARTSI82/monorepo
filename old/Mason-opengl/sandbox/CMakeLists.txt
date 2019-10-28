cmake_minimum_required(VERSION 3.14.3)

project(Sandbox VERSION 1.0.0 DESCRIPTION "Application run for the Mason library.")

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/bin)

set(CMAKE_CXX_STANDARD 17)

find_package(OpenGL REQUIRED)
find_library(COCOA_LIBRARY Cocoa)
find_package(OpenAL REQUIRED)
find_package(OpenCL REQUIRED)

add_executable(Sandbox src/cl_test.cpp)

target_include_directories(Sandbox PUBLIC ${PROJECT_SOURCE_DIR}/../api)
target_include_directories(Sandbox PUBLIC ${PROJECT_SOURCE_DIR}/../include)

#opengl
target_include_directories(Sandbox PUBLIC ${OPENGL_INCLUDE_DIR})
target_include_directories(Sandbox PUBLIC ${OPENAL_INCLUDE_DIR})
target_include_directories(Sandbox PUBLIC ${OPENCL_INCLUDE_DIR})

target_link_libraries(Sandbox PUBLIC ${OPENGL_gl_LIBRARY} ${OPENGL_glu_LIBRARY})
target_link_libraries(Sandbox PUBLIC ${OPENAL_LIBRARY})
target_link_libraries(Sandbox PRIVATE ${COCOA_LIBRARY})

target_link_libraries(Sandbox PUBLIC ${PROJECT_SOURCE_DIR}/../lib/libGLEW.2.1.0.dylib)
target_link_libraries(Sandbox PUBLIC ${PROJECT_SOURCE_DIR}/../lib/libglfw.3.4.dylib)
target_link_libraries(Sandbox PUBLIC "${PROJECT_SOURCE_DIR}/../build/lib/libMasonShare.1.0.0.dylib")

target_link_libraries(Sandbox PUBLIC "-framework OpenCL")

set_target_properties(Sandbox PROPERTIES
        ARCHIVE_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        LIBRARY_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        RUNTIME_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/bin"
        )
