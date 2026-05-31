cmake_minimum_required(VERSION 3.13)

project(statlog LANGUAGES C)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_STANDARD_REQUIRED ON)

find_library(LIBBPF bpf REQUIRED)
find_library(LIBELF elf REQUIRED)
find_library(LIBZ z REQUIRED)

# BPF build step
set(BPF_SRC_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/bpf")
add_custom_command(
  OUTPUT "${BPF_SRC_DIR}/statlog.skel.h" "${BPF_SRC_DIR}/statlog.bpf.o" "${BPF_SRC_DIR}/vmlinux.h"
  COMMAND ./compile.sh
  WORKING_DIRECTORY ${BPF_SRC_DIR}
  MAIN_DEPENDENCY ${BPF_SRC_DIR}/statlog.bpf.c
  DEPENDS ${BPF_SRC_DIR}/compile.sh /sys/kernel/btf/vmlinux
  COMMENT "Generating BPF skeleton from ${BPF_SRC_DIR}/statlog.bpf.c"
  VERBATIM
)

add_executable(statlog src/main.c src/bpf/statlog.skel.h)
target_include_directories(statlog PUBLIC src src/bpf)

target_compile_options(statlog PUBLIC -Os -flto -mtune=native -march=native -Wall -Wextra)
target_link_options(statlog PUBLIC -Os -flto -mtune=native -march=native -s -Wall -Wextra)

target_link_libraries(statlog ${LIBBPF} ${LIBELF} ${LIBZ})

set_property(TARGET statlog APPEND PROPERTY
  ADDITIONAL_CLEAN_FILES
  "${BPF_SRC_DIR}/statlog.skel.h" "${BPF_SRC_DIR}/statlog.bpf.o" "${BPF_SRC_DIR}/vmlinux.h")
