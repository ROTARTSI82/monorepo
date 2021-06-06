"""
Spec for rcmp files:
All integers are big-endian (network byte order) because little endian is dumb and whoever invented it should
be crucified.

"RCMP"
<uint16_t compatVersion>
<uint16_t specificVersion>
[HUFFMAN TREE:
    Chunks have 8-bit header. possible chunks include "n" for node, "l" for leaf.
    Leaf chunks are 16 bits, with the 2nd byte being the value. Node chunks are 8-bits (only the header).
    For example:
    "nnlalblc" encodes this tree:

     root
    /    \
   *      c
  / \
 a   b

]
"B" # for "begin encoding"
<uint32_t bitLength> # maximum 530 mb of compressed data
[Huffman encoded bytes:
    <uint32_t uncompressedBytes> # maximum 4gb of uncompressed data
    Chunks have 8-bit headers.
    ptr
        <8-bit header>
        <32-bit starting index>
        <32-bit size>

    rep
        <8-bit header>
        <32-bit #reps>
]:

"""

import struct
import argparse
from textwrap import wrap
from os import urandom
from enum import Enum

COMPAT_VERSION = 0
MICRO_VERSION = 0

"""
enum compression_chunk_type_t {
    e_node = 'n', // node of huffman tree
    e_leaf = 'l', // leaf node in huffman tree. value is next byte
    e_begin_encoding = 'B',

    e_data = 'd', // 4 byte length. 4gb max. do we even need chunks bigger than 65kb?
    e_datas = 's', //  2 byte length 65kb max
    e_datab = 'b', // 1 byte length 256bytes max
    e_ptr = 'p', // pointer to decoded data. 32-bit index, 32-bit size. this wastes a lot of space lol
    e_rep = 'r', // Repeat the last referenced region. 16-bit number of repetitions
};
"""


class CmpChunkType(Enum):
    eNode = b"n"
    eLeaf = b"l"
    eBeginEncoding = b"B"
    eData = b"d"
    eDatas = b"s"
    eDatab = b"b"

    ePtr = b"p"

    eDistLen = b"D"

    eRepLarge = b"R"
    eRepMed = b"m"
    eRepSmall = b"r"


class HuffmanNode:
    def __init__(self):
        self.c0 = None
        self.c1 = None
        self.val = -1
        self.freq = 0

    def __str__(self):
        return "[f = %s, v = %s, c0 = %s, c1 - %s]" % (self.freq, self.val, str(self.c0), str(self.c1))

    def add(self, enc):
        if self.val == -1:
            enc.add_node()
            self.c0.add(enc)
            self.c1.add(enc)
        else:
            enc.add_leaf(self.val)


class Encoder:
    def __init__(self):
        self.compressed = b"RCMP" + struct.pack("!H", COMPAT_VERSION) + struct.pack("!H", MICRO_VERSION)
        self.pre_huffman = b""
        self.final_size = 0
        self.region_size = 0

    def add_node(self):
        self.compressed += CmpChunkType.eNode.value

    def add_leaf(self, val):
        self.compressed += CmpChunkType.eLeaf.value
        self.compressed += struct.pack("!B", val)

    def add_data(self, dat):
        self.pre_huffman += CmpChunkType.eData.value
        self.pre_huffman += struct.pack("!I", len(dat))
        self.pre_huffman += dat

        self.final_size += len(dat)
        self.region_size = len(dat)

    def add_datas(self, dat):
        self.pre_huffman += CmpChunkType.eDatas.value
        self.pre_huffman += struct.pack("!H", len(dat))
        self.pre_huffman += dat

        self.final_size += len(dat)
        self.region_size = len(dat)

    def add_datab(self, dat):
        self.pre_huffman += CmpChunkType.eDatab.value
        self.pre_huffman += struct.pack("!B", len(dat))
        self.pre_huffman += dat

        self.final_size += len(dat)
        self.region_size = len(dat)

    def add_ptr(self, begin_index, size):
        self.pre_huffman += CmpChunkType.ePtr.value
        self.pre_huffman += struct.pack("!I", begin_index)
        self.pre_huffman += struct.pack("!I", size)

        self.final_size += size
        self.region_size = size

    def add_dist_len_enc(self, dist, length):
        self.pre_huffman += CmpChunkType.eDistLen.value
        self.pre_huffman += struct.pack("!I", dist)
        self.pre_huffman += struct.pack("!I", length)

        self.final_size += length
        self.region_size = length

    def add_repl(self, rep_times):
        self.pre_huffman += CmpChunkType.eRepLarge.value
        self.pre_huffman += struct.pack("!I", rep_times)

        self.final_size += self.region_size * rep_times

    def add_repm(self, rep_times):
        self.pre_huffman += CmpChunkType.eRepMed.value
        self.pre_huffman += struct.pack("!H", rep_times)

        self.final_size += self.region_size * rep_times

    def add_reps(self, rep_times):
        self.pre_huffman += CmpChunkType.eRepSmall.value
        self.pre_huffman += struct.pack("!B", rep_times)

        self.final_size += self.region_size * rep_times

    def dynamic_add_data(self, dat):
        if len(dat) < 256:
            self.add_datab(dat)
        elif len(dat) < 65536:
            self.add_datas(dat)
        else:
            self.add_data(dat)

    def add_autocompress(self, txt):
        proc_head = 0
        start = self.final_size

        # test = []

        data_block = b""
        while proc_head < len(txt):
            search_head = 0
            longest_match = -1
            longest_length = 0
            while search_head < proc_head:
                length = 0
                while proc_head + length < len(txt):
                    if txt[search_head + length] == txt[proc_head + length]:
                        length += 1
                    else:
                        break

                if length > longest_length:
                    longest_length = length
                    longest_match = search_head
                search_head += 1

            # this isn't optimal
            if longest_length > 9:  # ptrs take 9 bytes so we only save space if the sequence pointed to is > 9 bytes
                self.dynamic_add_data(data_block)

                data_block = b""

                # self.add_ptr(start + longest_match, longest_length)
                dist = proc_head - longest_match
                # print("Dist = %s, length = %s" % (dist, longest_length))
                self.add_dist_len_enc(dist, longest_length)

                proc_head += longest_length
            else:
                data_block += struct.pack("!B", txt[proc_head])
                proc_head += 1

        if data_block:
            self.dynamic_add_data(data_block)

    def finalize(self):
        self.pre_huffman = struct.pack("!I", self.final_size) + self.pre_huffman

        huff_dict = {}
        for i in self.pre_huffman:
            if i not in huff_dict:
                huff_dict[i] = HuffmanNode()
                huff_dict[i].val = i
                huff_dict[i].freq = 1
            else:
                huff_dict[i].freq += 1

        hls = sorted(list(huff_dict.values()), key=lambda x: x.freq)  # most frequent is first

        while len(hls) > 1:
            c0 = hls[0]
            c1 = hls[1]
            ins = HuffmanNode()
            ins.freq = c0.freq + c1.freq
            ins.c1 = c1
            ins.c0 = c0
            hls = hls[2:]

            hls.append(ins)
            hls = sorted(hls, key=lambda x: x.freq)

        hls[0].add(self)

        huff_dict = {}

        def recurse_add(n, s):
            if n.val == -1:
                recurse_add(n.c0, s + "0")
                recurse_add(n.c1, s + "1")
            else:
                huff_dict[n.val] = s

        recurse_add(hls[0], "")

        bits = ""
        for i in self.pre_huffman:
            bits += huff_dict[i]

        self.compressed += CmpChunkType.eBeginEncoding.value
        self.compressed += struct.pack("!I", len(bits))

        for i in wrap(bits, 8):
            while len(i) < 8:
                i += "0"
            self.compressed += struct.pack("!B", int("0b" + i, 2))

        print("=" * 16 + " [ REPORT ] " + "=" * 16)
        print("Original Data:\t\t", self.final_size)
        print("Lempel-Ziv style:\t", len(self.pre_huffman), "(%.3f%% less)" %
              ((self.final_size - len(self.pre_huffman)) * 100 / self.final_size))

        print("Huffman Coded:\t\t", len(self.compressed), "(%.3f%% less than plain, %.3f%% less than Lempel-Ziv)" %
              ((self.final_size - len(self.compressed)) * 100 / self.final_size,
               (len(self.pre_huffman) - len(self.compressed)) * 100 / len(self.pre_huffman)))
        return self.compressed


raw_data2 = b"""
I am Sam. I am Sam. Sam-I-Am.

That Sam-I-Am! That Sam-I-Am! I do not like that Sam-I-Am!

Do you like green eggs and ham?

I do not like them, Sam-I-Am.
I do not like green eggs and ham.

Would you like them here or there?

I would not like them here or there.
I would not like them anywhere.
I do not like green eggs and ham.
I do not like them, Sam-I-Am.

Would you like them in a house?
Would you like them with a mouse?

I do not like them in a house.
I do not like them with a mouse.
I do not like them here or there.
I do not like them anywhere.
I do not like green eggs and ham.
I do not like them, Sam-I-Am.

Would you eat them in a box?
Would you eat them with a fox?

Not in a box. Not with a fox.
Not in a house. Not with a mouse.
I would not eat them here or there.
I would not eat them anywhere.
I would not eat green eggs and ham.
I do not like them, Sam-I-Am.

Would you? Could you? In a car?
Eat them! Eat them! Here they are.

I would not, could not, in a car.

You may like them. You will see.
You may like them in a tree!

I would not, could not in a tree.
Not in a car! You let me be.

[....]

Could you, would you, with a goat?

I would not, could not with a goat!

Would you, could you, on a boat?

I could not, would not, on a boat.
I will not, will not, with a goat.
I will not eat them in the rain.
Not in the dark! Not in a tree!
Not in a car! You let me be!
I do not like them in a box.
I do not like them with a fox.
I will not eat them in a house.
I do not like them with a mouse.
I do not like them here or there.
I do not like them anywhere!
I do not like green eggs and ham!
I do not like them, Sam-I-Am.

You do not like them. So you say.
Try them! Try them! And you may.
Try them and you may, I say.

Sam! If you let me be,
I will try them. You will see.

(... And he tries them ...)

Say! I like green eggs and ham!
I do! I like them, Sam-I-Am!
And I would eat them in a boat.
And I would eat them with a goat...
And I will eat them, in the rain.
And in the dark. And on a train.
And in a car. And in a tree.
They are so good, so good, you see!
So I will eat them in a box.
And I will eat them with a fox.
And I will eat them in a house.
And I will eat them with a mouse.
And I will eat them here and there.
Say! I will eat them anywhere!
I do so like green eggs and ham!
Thank you! Thank you, Sam-I-Am.\0
""".strip()

enc = Encoder()
enc.add_autocompress(bytes(raw_data2, 'utf-8'))
enc.finalize()

# with open("./res/test.rcmp", 'wb') as fp:
#     fp.write(enc.compressed)
