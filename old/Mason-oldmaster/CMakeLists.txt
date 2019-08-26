cmake_minimum_required(VERSION 3.9)
project(Mason VERSION 1.0.0 DESCRIPTION "Mason description")

set(CMAKE_CXX_STANDARD 17)

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
add_library(Mason src/mason.cpp src/application.cpp src/thread.cpp src/log.cpp src/masonpch.cpp ${PCHSrc}
        api/mason/log.h api/mason/application.h api/mason/mason.h api/mason/masonpch.h api/mason/thread.h api/mason/event.h src/event.cpp)

set_target_properties(Mason PROPERTIES
        VERSION ${PROJECT_VERSION}
        SOVERSION 1.0.0
        PUBLIC_HEADER api/mason/mason.h
        ARCHIVE_OUTPUT_DIRECTORY /mason.cpp)

set_target_properties(Mason PROPERTIES
        VERSION ${PROJECT_VERSION}
        SOVERSION 1.0.0
        PUBLIC_HEADER api/mason/mason.h
        ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
        )

configure_file(build/mason.pc.in mason.pc)

target_include_directories(Mason PUBLIC src)
target_include_directories(Mason PRIVATE api)
target_include_directories(Mason PRIVATE include)
target_include_directories(Mason PUBLIC .)

target_link_libraries(Mason PUBLIC ${PROJECT_SOURCE_DIR}/lib/libspdlog.a)


