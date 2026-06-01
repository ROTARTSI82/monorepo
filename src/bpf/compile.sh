BPFTOOL=${BPFTOOL:-bpftool}
BPF_CLANG=${BPF_CLANG:-clang}
LLVM_STRIP=${LLVM_STRIP:-llvm-strip}
ARCH=${ARCH:-x86}
BPF_CPU=${BPF_CPU:-probe}
BTF_PATH=${BTF_PATH:-/sys/kernel/btf/vmlinux}

if [ ! -f vmlinux.h ]; then
    if [ -f "$BTF_PATH" ]; then
        $BPFTOOL btf dump file "$BTF_PATH" format c > vmlinux.h
    else
        echo "Error: vmlinux.h not found and $BTF_PATH not available."
        exit 1
    fi
fi
$BPF_CLANG -g -Os \
      -target bpf -D__TARGET_ARCH_$ARCH \
      -mcpu=$BPF_CPU \
      -fno-asynchronous-unwind-tables \
      -fmerge-all-constants \
      -Wno-missing-declarations \
      -c statlog.bpf.c -o statlog.bpf.o
$LLVM_STRIP -g statlog.bpf.o
$BPFTOOL gen skeleton statlog.bpf.o > statlog.skel.h
