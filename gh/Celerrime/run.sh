export CC=$(which gcc)
export CXX=$(which g++)

echo $CC
echo $CXX

mkdir build
cd build
cmake -DMAKE_C_COMPILER=$CC -DCMAKE_CXX_COMPILER=$CXX ..
cmake --build .
