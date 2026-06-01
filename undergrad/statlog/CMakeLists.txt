cmake_minimum_required(VERSION 3.13)

project(statlog LANGUAGES C)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_STANDARD_REQUIRED ON)

find_library(LIBBPF bpf REQUIRED)
find_library(LIBELF elf REQUIRED)
find_library(LIBZ z REQUIRED)

set(VMLINUX_BTF_PATH "/sys/kernel/btf/vmlinux" CACHE FILEPATH "Path to vmlinux BTF file")

# BPF build step
set(BPF_SRC_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/bpf")
add_custom_command(
  OUTPUT "${BPF_SRC_DIR}/statlog.skel.h" "${BPF_SRC_DIR}/statlog.bpf.o" "${BPF_SRC_DIR}/vmlinux.h"
  COMMAND bpftool btf dump file "${VMLINUX_BTF_PATH}" format c > vmlinux.h
  COMMAND clang -g -Os -target bpf -D__TARGET_ARCH_x86 -mcpu=probe -fno-asynchronous-unwind-tables -fmerge-all-constants -Wno-missing-declarations -c statlog.bpf.c -o statlog.bpf.o
  COMMAND llvm-strip -g statlog.bpf.o
  COMMAND bpftool gen skeleton statlog.bpf.o > statlog.skel.h
  WORKING_DIRECTORY ${BPF_SRC_DIR}
  MAIN_DEPENDENCY ${BPF_SRC_DIR}/statlog.bpf.c
  COMMENT "Generating BPF skeleton from ${BPF_SRC_DIR}/statlog.bpf.c using ${VMLINUX_BTF_PATH}"
  VERBATIM
)

add_executable(statlog src/main.c src/bpf/statlog.skel.h)
target_include_directories(statlog PUBLIC src src/bpf)

set(STATLOG_FLAGS -Wall -Wextra -march=native -mtune=native
    -Os -flto -fno-asynchronous-unwind-tables -fmerge-all-constants)
target_compile_options(statlog PUBLIC ${STATLOG_FLAGS})
target_link_options(statlog PUBLIC ${STATLOG_FLAGS} -s)

target_link_libraries(statlog ${LIBBPF} ${LIBELF} ${LIBZ})

set_property(TARGET statlog APPEND PROPERTY
  ADDITIONAL_CLEAN_FILES
  "${BPF_SRC_DIR}/statlog.skel.h" "${BPF_SRC_DIR}/statlog.bpf.o" "${BPF_SRC_DIR}/vmlinux.h")
