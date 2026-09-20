#!/bin/bash

cmake -S. -Bbuild

cd build

cmake --build . -j 8

cd ..
