# ByteGPT

This is a byte-level transformer trained on linux kernel `*.c` source code.

Online demo and blog post at https://students.washington.edu/granty29/bytegpt/. Source code for the demo and blog available at https://github.com/ROTARTSI82/blog/, primarily at `src/components/2026/ByteGPT.tsx` in that repo.

PyTorch training code in `train.py`, model implemented in `charmodel.py`.

C++ inference engine implemented in `src/model.cpp` in only 300 lines. WASM api defined in `src/wasm.cpp`, and an example of running inference is found at `src/testing.cpp`.

Training data was generated using `gendata.fish`, pulled from linux kernel commit `518e5b794c06c0f0eb40df3e202274a66202c137`.
