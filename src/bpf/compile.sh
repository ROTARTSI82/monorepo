bpftool btf dump file /sys/kernel/btf/vmlinux format c > vmlinux.h
clang -g -O3 -target bpf -D__TARGET_ARCH_x86 -c statlog.bpf.c -o statlog.bpf.o
bpftool gen skeleton statlog.bpf.o > statlog.skel.h
