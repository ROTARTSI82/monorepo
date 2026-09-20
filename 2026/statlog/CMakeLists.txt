cmake_minimum_required(VERSION 3.13)

project(statlog LANGUAGES C)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_STANDARD_REQUIRED ON)

add_executable(statlog src/main.c)
target_include_directories(statlog PUBLIC src src)

set(STATLOG_FLAGS -Wall -Wextra -Os -flto -fno-asynchronous-unwind-tables -fmerge-all-constants)
target_compile_options(statlog PUBLIC ${STATLOG_FLAGS})
target_link_options(statlog PUBLIC ${STATLOG_FLAGS} -s)

set(NDISKS  1 CACHE STRING "number of disks")
set(DISKS "{\"nvme0n1\"}" CACHE STRING
       "disks in /dev/ to monitor for diskr diskw")

set(NCPU  16 CACHE STRING "number of cpus")

set(NDEVS  1 CACHE STRING "number of network devices")
set(DEVS "{\"enp191s0\"}" CACHE STRING
       "network devices (see /proc/net/dev) to monitor for tx rx")


target_compile_definitions(statlog PUBLIC
  -DNCPU=${NCPU}
  -DNDISKS=${NDISKS} -DDISKS=${DISKS}
  -DNDEVS=${NDEVS} -DDEVS=${DEVS})

install(TARGETS statlog DESTINATION bin)
