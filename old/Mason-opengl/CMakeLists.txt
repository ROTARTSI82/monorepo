cmake_minimum_required(VERSION 3.14.3)
project(Mason VERSION 1.0.0 DESCRIPTION "Mason description")

set(CMAKE_CXX_STANDARD 17)

find_package(OpenGL REQUIRED)

include(GNUInstallDirs)

set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS ON CACHE BOOL "" FORCE)

add_library(Mason STATIC src/mason.cpp api/mason.h)

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


target_link_libraries(Mason PUBLIC ${OPENGL_gl_LIBRARY} ${OPENGL_glu_LIBRARY})
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

add_library(MasonShare SHARED src/mason.cpp api/mason.h)

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

target_link_libraries(MasonShare PUBLIC ${OPENGL_gl_LIBRARY} ${OPENGL_glu_LIBRARY})
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libglfw.3.4.dylib)
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libspdlog.dylib)
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libimgui.a)
target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libGLEW.2.1.0.dylib)
# glm and stb not here

install(TARGETS MasonShare
        LIBRARY DESTINATION ${PROJECT_SOURCE_DIR}/build/lib
        PUBLIC_HEADER DESTINATION ${PROJECT_SOURCE_DIR}/build/include)

install(FILES ${PROJECT_SOURCE_DIR}/build/mason.pc
        DESTINATION ${PROJECT_SOURCE_DIR}/build/pkgconfig)
