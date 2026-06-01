bpftool btf dump file /sys/kernel/btf/vmlinux format c > vmlinux.h
clang -g -Os \
      -target bpf -D__TARGET_ARCH_x86 \
      -mcpu=probe \
      -fno-asynchronous-unwind-tables \
      -fmerge-all-constants \
      -Wno-missing-declarations \
      -c statlog.bpf.c -o statlog.bpf.o
llvm-strip -g statlog.bpf.o
bpftool gen skeleton statlog.bpf.o > statlog.skel.h
