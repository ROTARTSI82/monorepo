cmake_minimum_required(VERSION 3.13)

project(statlog LANGUAGES C)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_STANDARD_REQUIRED ON)

find_library(LIBBPF bpf REQUIRED)
find_library(LIBELF elf REQUIRED)
find_library(LIBZ z REQUIRED)

add_executable(statlog src/main.c)
target_include_directories(statlog PUBLIC src src/bpf)

target_compile_options(statlog PUBLIC -Os -flto -mtune=native -march=native -Wall -Wextra)
target_link_options(statlog PUBLIC -Os -flto -mtune=native -march=native -s -Wall -Wextra)

target_link_libraries(statlog ${LIBBPF} ${LIBELF} ${LIBZ})
