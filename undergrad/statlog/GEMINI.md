This is a C project to periodically record stats about my Fedora 44 server running on my framework desktop (32 GB).

I am using eBPF to periodically log bytes read and written to disk and over the network. All code is in the src/ directory.

The focus is on craft:
1) do the bare minimum work possible and as efficiently as possible
2) be as simple and readable as possible
3) produce the smallest binary size possible
4) robustness and correctness. do everything idiomatically and safely. do the *right* thing in the *right* way.

the project is built with cmake, using CMakeLists.txt with -G Ninja and clang. the build directory is ./build/

