cmake_minimum_required(VERSION 3.13)

project(statlog LANGUAGES C)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_STANDARD_REQUIRED ON)

find_library(LIBBPF bpf REQUIRED)
find_library(LIBELF elf REQUIRED)
find_library(LIBZ z REQUIRED)

find_program(BPFTOOL bpftool REQUIRED)
find_program(CLANG clang REQUIRED)
find_program(LLVM_STRIP llvm-strip REQUIRED)

# Detect architecture for BPF
if(CMAKE_SYSTEM_PROCESSOR STREQUAL "x86_64")
    set(BPF_ARCH "x86")
elseif(CMAKE_SYSTEM_PROCESSOR STREQUAL "aarch64")
    set(BPF_ARCH "arm64")
else()
    set(BPF_ARCH "${CMAKE_SYSTEM_PROCESSOR}")
endif()

set(VMLINUX_BTF_PATH "/sys/kernel/btf/vmlinux" CACHE FILEPATH "Path to vmlinux BTF file")

# BPF build step
set(BPF_SRC_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/bpf")
add_custom_command(
  OUTPUT "${BPF_SRC_DIR}/statlog.skel.h" "${BPF_SRC_DIR}/statlog.bpf.o" "${BPF_SRC_DIR}/vmlinux.h"
  COMMAND ${CMAKE_COMMAND} -E env BPFTOOL=${BPFTOOL} CLANG=${CLANG} LLVM_STRIP=${LLVM_STRIP} ARCH=${BPF_ARCH} ./compile.sh "${VMLINUX_BTF_PATH}"
  WORKING_DIRECTORY ${BPF_SRC_DIR}
  MAIN_DEPENDENCY ${BPF_SRC_DIR}/statlog.bpf.c
  DEPENDS "${BPF_SRC_DIR}/compile.sh"
  COMMENT "Generating BPF skeleton for ${BPF_ARCH} from ${BPF_SRC_DIR}/statlog.bpf.c using ${VMLINUX_BTF_PATH}"
  VERBATIM
)

add_executable(statlog src/main.c src/bpf/statlog.skel.h)
target_include_directories(statlog PUBLIC src src/bpf)

set(STATLOG_FLAGS -Wall -Wextra -Os -flto -fno-asynchronous-unwind-tables -fmerge-all-constants)
target_compile_options(statlog PUBLIC ${STATLOG_FLAGS})
target_link_options(statlog PUBLIC ${STATLOG_FLAGS} -s)

target_link_libraries(statlog ${LIBBPF} ${LIBELF} ${LIBZ})

install(TARGETS statlog DESTINATION bin)

set_property(TARGET statlog APPEND PROPERTY
  ADDITIONAL_CLEAN_FILES
  "${BPF_SRC_DIR}/statlog.skel.h" "${BPF_SRC_DIR}/statlog.bpf.o" "${BPF_SRC_DIR}/vmlinux.h")
