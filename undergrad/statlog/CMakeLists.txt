cmake_minimum_required(VERSION 3.13)

project(statlog LANGUAGES C)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_STANDARD_REQUIRED ON)

add_executable(statlog src/main.c)
target_include_directories(statlog PUBLIC src src)

set(STATLOG_FLAGS -Wall -Wextra -Os -flto -fno-asynchronous-unwind-tables -fmerge-all-constants)
target_compile_options(statlog PUBLIC ${STATLOG_FLAGS})
target_link_options(statlog PUBLIC ${STATLOG_FLAGS} -s)

set(DISKS "{\"nvme0n1\"}")

target_compile_definitions(statlog PUBLIC
  -DNCPU=16
  -DNDISKS=1 -DDISKS=${DISKS})

install(TARGETS statlog DESTINATION bin)
