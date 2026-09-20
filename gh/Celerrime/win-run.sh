mkdir build-win

cd build-win || exit

cmake -DCEL_MINGW_MODE=ON -DCMAKE_TOOLCHAIN_FILE="../misc/mingw.cmake" ..
cmake --build . -j8
