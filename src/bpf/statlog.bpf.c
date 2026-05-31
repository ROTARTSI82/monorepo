#include "vmlinux.h"
#include <bpf/bpf_helpers.h>
#include <bpf/bpf_tracing.h>

struct {
	__uint(type, BPF_MAP_TYPE_PERCPU_ARRAY);
	__uint(max_entries, 1);
	__type(key, u32);
	__type(value, u64);
} net_tx SEC(".maps");

struct {
	__uint(type, BPF_MAP_TYPE_PERCPU_ARRAY);
	__uint(max_entries, 1);
	__type(key, u32);
	__type(value, u64);
} net_rx SEC(".maps");

struct {
	__uint(type, BPF_MAP_TYPE_PERCPU_ARRAY);
	__uint(max_entries, 1);
	__type(key, u32);
	__type(value, u64);
} disk_reads SEC(".maps");

struct {
	__uint(type, BPF_MAP_TYPE_PERCPU_ARRAY);
	__uint(max_entries, 1);
	__type(key, u32);
	__type(value, u64);
} disk_writes SEC(".maps");

static __always_inline void increment_map(void *map, u64 val)
{
	u32 key = 0;
	u64 *count = bpf_map_lookup_elem(map, &key);
	if (count)
		*count += val;
}

SEC("tp_btf/block_rq_complete")
int BPF_PROG(block_rq_complete, struct request *rq, int error, unsigned int nr_bytes)
{
	if ((rq->cmd_flags & 255) == REQ_OP_WRITE)
		increment_map(&disk_writes, nr_bytes);
	else if ((rq->cmd_flags & 255) == REQ_OP_READ)
		increment_map(&disk_reads, nr_bytes);
	return 0;
}

SEC("tp_btf/netif_receive_skb")
int BPF_PROG(netif_receive_skb, struct sk_buff *skb)
{
	increment_map(&net_rx, skb->len);
	return 0;
}

SEC("tp_btf/net_dev_queue")
int BPF_PROG(net_dev_queue, struct sk_buff *skb)
{
	increment_map(&net_tx, skb->len);
	return 0;
}

char LICENSE[] SEC("license") = "GPL";
