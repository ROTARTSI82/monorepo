cd build
cmake ..
make -j
sudo make install

cd ../sandbox/build
cmake ..
make -j
# sudo make install

./Sandbox

sudo chown -R Grant /Users/Grant/Desktop/CLion/Mason
