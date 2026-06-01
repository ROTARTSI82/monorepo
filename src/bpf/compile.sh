if [ ! -f vmlinux.h ]; then
    BTF_PATH=${1:-/sys/kernel/btf/vmlinux}
    if [ -f "$BTF_PATH" ]; then
        bpftool btf dump file "$BTF_PATH" format c > vmlinux.h
    else
        echo "Error: vmlinux.h not found and $BTF_PATH not available."
        exit 1
    fi
fi
clang -g -Os \
      -target bpf -D__TARGET_ARCH_x86 \
      -mcpu=probe \
      -fno-asynchronous-unwind-tables \
      -fmerge-all-constants \
      -Wno-missing-declarations \
      -c statlog.bpf.c -o statlog.bpf.o
llvm-strip -g statlog.bpf.o
bpftool gen skeleton statlog.bpf.o > statlog.skel.h
