"""
enum compression_chunk_type_t {
    e_node = 'n', // node of huffman tree
    e_leaf = 'l', // leaf node in huffman tree. value is next byte
    e_begin_encoding = 'B',
"""

compressed = b"RCMP\x00\x00\x00\x00"

import struct

def add_node():
    global compressed
    compressed += b'n'

def add_leaf(val):
    global compressed
    print ("leaf value ", val[0])
    compressed += b'l'
    compressed += struct.pack("!B", val[0])

class HuffmanNode:
    def __init__(self):
        self.c0 = 0
        self.c1 = 0
        self.val = -1

    def add(self):
        if self.val == -1:
            add_node()
            self.c0.add()
            self.c1.add()
        else:
            add_leaf(self.val)

root = HuffmanNode()
root.c0 = HuffmanNode()
root.c1 = HuffmanNode()
root.c1.val = b't'

root.c0.c0 = HuffmanNode()
root.c0.c1 = HuffmanNode()
root.c0.c0.val = b'e'
root.c0.c1.val = b's'

root.add()

compressed += b'B'
compressed += struct.pack("!Q", 6)
compressed += struct.pack("!B", 0b10001100)

print (compressed)

with open("./res/test.rcmp", "wb") as fp:
    fp.write(compressed)



