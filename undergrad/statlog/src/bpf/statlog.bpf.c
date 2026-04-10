#include <linux/bpf.h>
#include <bpf/bpf_helpers.h>

// 1. Declare our global variables! 
// libbpf will automatically map these to user-space memory.
__u64 disk_reads = 0;
__u64 disk_writes = 0;
__u64 net_rx = 0;
__u64 net_tx = 0;

// 2. Define the memory layout of the tracepoint arguments.
// These map exactly to the kernel's internal tracepoint format.
struct block_rq_complete_args {
    unsigned short common_type;
    unsigned char common_flags;
    unsigned char common_preempt_count;
    int common_pid;
    unsigned int dev;
    int __pad1; // Added padding to align sector at offset 16
    unsigned long long sector;
    unsigned int nr_sector;
    int errors;
    unsigned short ioprio; // Missing in previous version
    char rwbs[10]; // Size 10 in kernel, was 8
};

struct net_args {
    unsigned short common_type;
    unsigned char common_flags;
    unsigned char common_preempt_count;
    int common_pid;
    void *skbaddr;
    unsigned int len; // Length of the network packet in bytes
};

// --- DISK I/O HOOK ---
SEC("tracepoint/block/block_rq_complete")
int handle_block_rq_complete(struct block_rq_complete_args *ctx) {
    __u64 bytes = ctx->nr_sector * 512; // 1 sector = 512 bytes
    
    // rwbs can contain multiple flags (e.g., "WS", "RA").
    // We search for 'W' or 'R' in the string.
    int is_write = 0;
    int is_read = 0;

    #pragma unroll
    for (int i = 0; i < 10; i++) {
        if (ctx->rwbs[i] == 'W') is_write = 1;
        else if (ctx->rwbs[i] == 'R') is_read = 1;
        else if (ctx->rwbs[i] == '\0') break;
    }

    if (is_write) {
        __sync_fetch_and_add(&disk_writes, bytes);
    } else if (is_read) {
        __sync_fetch_and_add(&disk_reads, bytes);
    }
    
    return 0;
}

// --- NETWORK I/O HOOKS ---
SEC("tracepoint/net/netif_receive_skb")
int handle_net_rx(struct net_args *ctx) {
    __sync_fetch_and_add(&net_rx, ctx->len);
    return 0;
}

SEC("tracepoint/net/net_dev_queue")
int handle_net_tx(struct net_args *ctx) {
    __sync_fetch_and_add(&net_tx, ctx->len);
    return 0;
}

// The kernel requires a GPL license to load tracepoint BPF programs
char LICENSE[] SEC("license") = "GPL";

