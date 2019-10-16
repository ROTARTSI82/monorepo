cmake_minimum_required(VERSION 3.14.3)
project(Mason VERSION 1.0.0 DESCRIPTION "Mason game engine for OpenGL")

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/bin)

set(CMAKE_CXX_STANDARD 17)

find_package(OpenGL REQUIRED)
find_package(OpenAL REQUIRED)

include(GNUInstallDirs)

set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS ON CACHE BOOL "" FORCE)

add_library(Mason STATIC src/mason/transform_info.cpp api/mason/transform_info.h api/mason/camera.h src/mason/camera.cpp
        api/mason/app_node.h api/mason/layer.h src/mason/app_node.cpp src/mason/layer.cpp api/mason/gl/gl_window.h
        api/mason/gl/gl_core.h src/mason/gl/gl_core.cpp src/mason/gl/gl_window.cpp src/mason/log.cpp api/mason/log.h
        api/mason/mason.h src/mason/mason.cpp api/mason/gl/vertex_buffer.h src/mason/gl/vertex_buffer.cpp api/mason/gl/vertex_data.h
        api/mason/gl/vertex_array.h src/mason/gl/vertex_array.cpp src/mason/gl/vb_layout.cpp api/mason/gl/vb_layout.h
        src/mason/gl/vb_attribute.cpp api/mason/gl/vb_attribute.h src/mason/gl/index_buffer.cpp api/mason/gl/index_buffer.h
        src/mason/gl/shader_program.cpp api/mason/gl/shader_program.h src/mason/gl/texture2d.cpp api/mason/gl/texture2d.h
        src/mason/image.cpp api/mason/image.h src/mason/rigidbody.cpp api/mason/rigidbody.h src/mason/gl/gl_event.cpp
        api/mason/gl/gl_event.h src/mason/gl/gl_event_handler.cpp api/mason/gl/gl_event_handler.h src/mason/task_pool.cpp
        api/mason/task_pool.h src/mason/thread_pool.cpp api/mason/thread_pool.h src/mason/al/al_source.cpp api/mason/al/al_source.h api/mason/al/al_core.h api/mason/al/al_context.h api/mason/al/al_device.h src/mason/al/al_device.cpp src/mason/al/al_context.cpp src/mason/al/al_core.cpp api/mason/al/al_listener.h api/mason/al/al_buffer.h src/mason/al/al_listener.cpp src/mason/al/al_buffer.cpp)

set_target_properties(Mason PROPERTIES
        VERSION ${PROJECT_VERSION}
        SOVERSION 1
        ARCHIVE_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        LIBRARY_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        RUNTIME_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/bin"
        )

configure_file(${PROJECT_SOURCE_DIR}/build/mason.pc.in ${PROJECT_SOURCE_DIR}/build/mason.pc @ONLY)

target_include_directories(Mason PUBLIC src)
target_include_directories(Mason PUBLIC api)
target_include_directories(Mason PUBLIC include)
target_include_directories(Mason PUBLIC .)

#opengl
target_include_directories(Mason PUBLIC ${OPENGL_INCLUDE_DIR})

#openal
target_include_directories(Mason PUBLIC ${OPENAL_INCLUDE_DIR})


target_link_libraries(Mason PUBLIC ${OPENGL_gl_LIBRARY} ${OPENGL_glu_LIBRARY})
target_link_libraries(Mason PUBLIC ${OPENAL_LIBRARY})
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libglfw3.a)
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libspdlog.a)
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libimgui.a)
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libGLEW.a)
# glm and stb not here.

install(TARGETS Mason
        LIBRARY DESTINATION ${PROJECT_SOURCE_DIR}/build/lib
        PUBLIC_HEADER DESTINATION ${PROJECT_SOURCE_DIR}/build/include)

# ================[SHARED LIBRARY BUILD]========================

project(MasonShare VERSION 1.0.0 DESCRIPTION "MasonShare description")

add_library(MasonShare SHARED src/mason/transform_info.cpp api/mason/transform_info.h api/mason/camera.h src/mason/camera.cpp
        api/mason/app_node.h api/mason/layer.h src/mason/app_node.cpp src/mason/layer.cpp api/mason/gl/gl_window.h
        api/mason/gl/gl_core.h src/mason/gl/gl_core.cpp src/mason/gl/gl_window.cpp src/mason/log.cpp api/mason/log.h
        api/mason/mason.h src/mason/mason.cpp api/mason/gl/vertex_buffer.h src/mason/gl/vertex_buffer.cpp api/mason/gl/vertex_data.h
        api/mason/gl/vertex_array.h src/mason/gl/vertex_array.cpp src/mason/gl/vb_layout.cpp api/mason/gl/vb_layout.h
        src/mason/gl/vb_attribute.cpp api/mason/gl/vb_attribute.h src/mason/gl/index_buffer.cpp api/mason/gl/index_buffer.h
        src/mason/gl/shader_program.cpp api/mason/gl/shader_program.h src/mason/gl/texture2d.cpp api/mason/gl/texture2d.h
        src/mason/image.cpp api/mason/image.h src/mason/rigidbody.cpp api/mason/rigidbody.h src/mason/gl/gl_event.cpp
        api/mason/gl/gl_event.h src/mason/gl/gl_event_handler.cpp api/mason/gl/gl_event_handler.h src/mason/task_pool.cpp
        api/mason/task_pool.h src/mason/thread_pool.cpp api/mason/thread_pool.h src/mason/al/al_source.cpp api/mason/al/al_source.h api/mason/al/al_core.h api/mason/al/al_context.h api/mason/al/al_device.h src/mason/al/al_device.cpp src/mason/al/al_context.cpp src/mason/al/al_core.cpp api/mason/al/al_listener.h api/mason/al/al_buffer.h src/mason/al/al_listener.cpp src/mason/al/al_buffer.cpp)

set_target_properties(MasonShare PROPERTIES
        VERSION ${PROJECT_VERSION}
        SOVERSION 1
        ARCHIVE_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        LIBRARY_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        RUNTIME_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/bin"
        )


target_include_directories(MasonShare PUBLIC src)
target_include_directories(MasonShare PUBLIC api)
target_include_directories(MasonShare PUBLIC include)
target_include_directories(MasonShare PUBLIC .)

#opengl
target_include_directories(MasonShare PUBLIC ${OPENGL_INCLUDE_DIR})

#openal
target_include_directories(MasonShare PUBLIC ${OPENAL_INCLUDE_DIR})

target_link_libraries(MasonShare PUBLIC ${OPENGL_gl_LIBRARY} ${OPENGL_glu_LIBRARY})
target_link_libraries(MasonShare PUBLIC ${OPENAL_LIBRARY})
target_link_libraries(MasonShare PUBLIC ${PROJECT_SOURCE_DIR}/lib/libglfw.3.4.dylib)
target_link_libraries(MasonShare PUBLIC ${PROJECT_SOURCE_DIR}/lib/libspdlog.dylib)
target_link_libraries(MasonShare PUBLIC ${PROJECT_SOURCE_DIR}/lib/libimgui.a)
target_link_libraries(MasonShare PUBLIC ${PROJECT_SOURCE_DIR}/lib/libGLEW.2.1.0.dylib)
# glm and stb not here

install(TARGETS MasonShare
        LIBRARY DESTINATION ${PROJECT_SOURCE_DIR}/build/lib
        PUBLIC_HEADER DESTINATION ${PROJECT_SOURCE_DIR}/build/include)

install(FILES ${PROJECT_SOURCE_DIR}/build/mason.pc
        DESTINATION ${PROJECT_SOURCE_DIR}/build/pkgconfig)
