cmake_minimum_required(VERSION 3.10)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

project(mcserve)
file(GLOB_RECURSE MC_SOURCES src/**.cpp)
add_executable(mcserve ${MC_SOURCES})
target_include_directories(mcserve PUBLIC include)
target_include_directories(mcserve PRIVATE src)

target_link_options(mcserve PUBLIC -march=native -mtune=native -O0 -g3 -glldb -flto -Wall -Wextra)
target_compile_options(mcserve PUBLIC -march=native -mtune=native -O0 -g3 -glldb -flto -Wall -Wextra)

target_link_options(mcserve PUBLIC -fsanitize=address -fno-omit-frame-pointer)
target_compile_options(mcserve PUBLIC -fsanitize=address -fno-omit-frame-pointer)
