export CC=$(which gcc)
export CXX=$(which g++)

# export CC=$(which clang)
# export CXX=$(which clang++)

echo $CC
echo $CXX

mkdir build
cd build
cmake -GNinja -Wdev -Wdeprecated -DCMAKE_C_COMPILER:string=$CC -DCMAKE_CXX_COMPILER:string=$CXX ..
cmake --build . -j 8
