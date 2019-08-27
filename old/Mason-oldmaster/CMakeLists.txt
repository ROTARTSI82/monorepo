cmake_minimum_required(VERSION 3.9)
project(MasonShare VERSION 1.0.0 DESCRIPTION "Mason description")
project(Mason VERSION 1.0.0 DESCRIPTION "Mason description")

set(CMAKE_CXX_STANDARD 17)

include(GNUInstallDirs)

MACRO(ADD_MSVC_PRECOMPILED_HEADER PrecompiledHeader PrecompiledSource SourcesVar)
    IF (MSVC)
        GET_FILENAME_COMPONENT(PrecompiledBasename ${PrecompiledHeader} NAME_WE)
        SET(PrecompiledBinary "${PROJECT_SOURCE_DIR}/build/pch/${PrecompiledBasename}.pch")
        SET(Sources ${${SourcesVar}})

        SET_SOURCE_FILES_PROPERTIES(${PrecompiledSource}
                PROPERTIES COMPILE_FLAGS "/Yc\"${PrecompiledHeader}\" /Fp\"${PrecompiledBinary}\""
                OBJECT_OUTPUTS "${PrecompiledBinary}")
        SET_SOURCE_FILES_PROPERTIES(${Sources}
                PROPERTIES COMPILE_FLAGS "/Yu\"${PrecompiledHeader}\" /FI\"${PrecompiledHeader}\" /Fp\"${PrecompiledBinary}\""
                OBJECT_DEPENDS "${PrecompiledBinary}")
        # Add precompiled header to SourcesVar
        LIST(APPEND ${SourcesVar} ${PrecompiledSource})
    ENDIF (MSVC)
ENDMACRO(ADD_MSVC_PRECOMPILED_HEADER)

ADD_MSVC_PRECOMPILED_HEADER("api/mason/masonpch.h" "src/masonpch.cpp" PCHSrc)

add_library(Mason STATIC src/mason.cpp src/application.cpp src/thread.cpp src/log.cpp src/masonpch.cpp ${PCHSrc}
        api/mason/log.h api/mason/application.h api/mason/mason.h api/mason/masonpch.h api/mason/thread.h api/mason/event.h src/event.cpp api/mason/gl/window.h src/gl/window.cpp)
add_library(MasonShare SHARED src/mason.cpp src/application.cpp src/thread.cpp src/log.cpp src/masonpch.cpp ${PCHSrc}
        api/mason/log.h api/mason/application.h api/mason/mason.h api/mason/masonpch.h api/mason/thread.h api/mason/event.h src/event.cpp api/mason/gl/window.h src/gl/window.cpp)

set_target_properties(Mason PROPERTIES
        VERSION ${PROJECT_VERSION}
        SOVERSION 1
        PUBLIC_HEADER api/mason/masonpch.h
        ARCHIVE_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        LIBRARY_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        RUNTIME_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/bin"
        )

set_target_properties(MasonShare PROPERTIES
        VERSION ${PROJECT_VERSION}
        SOVERSION 1
        PUBLIC_HEADER api/mason/masonpch.h
        ARCHIVE_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        LIBRARY_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/lib"
        RUNTIME_OUTPUT_DIRECTORY "${PROJECT_SOURCE_DIR}/build/bin"
        )
#        PUBLIC_HEADER ${PROJECT_SOURCE_DIR}/api/mason/mason.h ${PROJECT_SOURCE_DIR}/api/mason/event.h ${PROJECT_SOURCE_DIR}/api/mason/log.h
#        ${PROJECT_SOURCE_DIR}/api/mason/application.h ${PROJECT_SOURCE_DIR}/api/mason/masonpch.h ${PROJECT_SOURCE_DIR}/api/mason/thread.h)

configure_file(${PROJECT_SOURCE_DIR}/build/mason.pc.in ${PROJECT_SOURCE_DIR}/build/mason.pc @ONLY)

target_include_directories(Mason PUBLIC src)
target_include_directories(Mason PRIVATE api)
target_include_directories(Mason PRIVATE include)
target_include_directories(Mason PUBLIC .)

target_include_directories(MasonShare PUBLIC src)
target_include_directories(MasonShare PRIVATE api)
target_include_directories(MasonShare PRIVATE include)
target_include_directories(MasonShare PUBLIC .)


install(TARGETS Mason
        LIBRARY DESTINATION ${PROJECT_SOURCE_DIR}/build/lib
        PUBLIC_HEADER DESTINATION ${PROJECT_SOURCE_DIR}/build/include)

install(TARGETS MasonShare
        LIBRARY DESTINATION ${PROJECT_SOURCE_DIR}/build/lib
        PUBLIC_HEADER DESTINATION ${PROJECT_SOURCE_DIR}/build/include)

install(FILES ${PROJECT_SOURCE_DIR}/build/mason.pc
        DESTINATION ${PROJECT_SOURCE_DIR}/build/pkgconfig)


target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libspdlog.a ${PROJECT_SOURCE_DIR}/lib/libglfw3.a ${PROJECT_SOURCE_DIR}/lib/libGLEW.a ${PROJECT_SOURCE_DIR}/lib/libglm_static.a)
target_link_libraries(MasonShare PUBLIC ${PROJECT_SOURCE_DIR}/lib/libspdlog.dylib ${PROJECT_SOURCE_DIR}/lib/libglfw.3.dylib ${PROJECT_SOURCE_DIR}/lib/libGLEW.2.1.0.dylib ${PROJECT_SOURCE_DIR}/lib/libglm_shared.dylib)

