cmake_minimum_required(VERSION 3.14)
project(Sandbox VERSION 1.0.0)

set(CMAKE_CXX_STANDARD 17)

add_executable(Sandbox src/sandbox.cpp)

find_package(OpenGL REQUIRED)
find_library(COCOA_LIBRARY Cocoa)


target_include_directories(Sandbox PRIVATE ../api)
target_include_directories(Sandbox PRIVATE ../include)
target_include_directories(Sandbox PRIVATE ../src)
target_include_directories(Sandbox PRIVATE ..)
target_include_directories(Sandbox PRIVATE ${OPENGL_INCLUDE_DIR})

target_link_libraries(Sandbox PRIVATE ${OPENGL_gl_LIBRARY} ${OPENGL_glu_LIBRARY})
target_link_libraries(Sandbox PRIVATE ${COCOA_LIBRARY})

#target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libglfw3.a)
#target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libGLEW.a)
#target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libspdlog.a)
#target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libglm_static.a)
#target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../build/lib/libMason.a)

target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libglfw.3.3.dylib)
target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libGLEW.2.1.0.dylib)
target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libspdlog.dylib)
target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../lib/libglm_shared.dylib)
target_link_libraries(Sandbox PRIVATE ${PROJECT_SOURCE_DIR}/../build/lib/libMasonShare.1.0.0.dylib)
