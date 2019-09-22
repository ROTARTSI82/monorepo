cmake .
make -j 4

mkdir -p sandbox/build
cd sandbox/build
cmake ..
make -j 4

bin/Sandbox
