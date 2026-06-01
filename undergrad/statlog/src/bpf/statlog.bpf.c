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
	if (error != 0) return 0;

	struct request_queue *q = rq->q;
	if (!q || !(q->disk)) return 0;

	// // Skip virtual devices to avoid double-counting (e.g. counting both LV and PV).
	// // Physical disks typically start with sd, nvme, vd, hd.
	// // We specifically exclude dm- (LVM/LUKS), loop, and md (RAID).
	// char c0 = q->disk->disk_name[0];
	// char c1 = q->disk->disk_name[1];
	// char c2 = q->disk->disk_name[2];

	// if (c0 == 'd' && c1 == 'm' && c2 == '-') return 0; // dm-X
	// if (c0 == 'l' && c1 == 'o' && c2 == 'o') return 0; // loopX
	// if (c0 == 'm' && c1 == 'd') {
	//    if (c2 >= '0' && c2 <= '9') return 0; // mdX
	// }

	blk_opf_t op = rq->cmd_flags & 255;
	if (op == REQ_OP_WRITE) // || op == REQ_OP_WRITE_ZEROES || op == REQ_OP_ZONE_APPEND)
		increment_map(&disk_writes, nr_bytes);
	else if (op == REQ_OP_READ)
		increment_map(&disk_reads, nr_bytes);
	return 0;
}

SEC("tp_btf/netif_receive_skb")
int BPF_PROG(netif_receive_skb, struct sk_buff *skb)
{
	struct net_device *dev = skb->dev;
	if (dev->flags & IFF_LOOPBACK)
		return 0;
	if (dev->rtnl_link_ops)
		return 0;

	increment_map(&net_rx, skb->len);
	return 0;
}

SEC("tp_btf/net_dev_queue")
int BPF_PROG(net_dev_queue, struct sk_buff *skb)
{
	struct net_device *dev = skb->dev;
	if (dev->flags & IFF_LOOPBACK)
		return 0;
	if (dev->rtnl_link_ops)
		return 0;

	increment_map(&net_tx, skb->len);
	return 0;
}

char LICENSE[] SEC("license") = "GPL";
