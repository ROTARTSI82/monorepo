clang -g -O3 -target bpf -D__TARGET_ARCH_x86 -c statlog.bpf.c -o statlog.bpf.o
bpftool gen skeleton statlog.bpf.o > statlog.skel.h
