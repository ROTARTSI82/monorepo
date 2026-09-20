#!/usr/bin/python3

import struct
import argparse

parser = argparse.ArgumentParser(description='Script Assembler for Celerrime')
parser.add_argument('files', nargs='+', help="Files to assemble")
parser.add_argument('-o', nargs='?', help="File to output the assembled binary to (out.txt by default)")

args = parser.parse_args()
args.o = "out.txt" if not args.o else args.o

if len(args.files) != 1:
    raise Exception("Only 1 file is supported right now")

outp = bytearray()

type_literals = [
    "u8", "u16", "u32", "u64",
    "i8", "i16", "i32", "i64",
    "f32", "f64",
]

struct_literals = [
    ('B', int), ('H', int), ('I', int), ('L', int),
    ('b', int), ('h', int), ('i', int), ('l', int),
    ('f', float), ('d', float)
]

instructions = [i.strip()[:-1] for i in """
    panic, nop,
    push8, push16, push32, push64, 
    pop, pop32, ind_pop,
    ind_jmp, ind_jmpif,
    ind_jabs, ind_jabsif,
    ind_call, ind_cabs,
    jmp, jmpif, jabs, jabsif, call, cabs, 
    ret,
    get_lbp, get_lsp, 
    dref_lbp, dref_lsp, 
    set_lbp, set_lsp, 
    dref_gbp, set_gbp,
    get_gbp, get_gsize,
    mod_gsp32, 
    mod_lsp, mod_lsp32, 
    get_pc, 
    dref, 
    set_ptr, 
    conv, 
    add, sub, mul, div, mod, 
    bit_xor, bit_and, bit_or, bit_compl_not, 
    bit_shl, bit_shr,
    bool_xor, bool_and, bool_or, bool_not, 
    ne, eq, lt, gt, le, ge, 

    ind_cfunc, cfunc,

    EXT_DBG_outp,
    """.split()]
print (instructions)

# labels are always 32-bit signed integers!
labels = {}
to_replace = []

with open(args.files[0], 'r') as fp:
    contents = fp.read().split()

for token in contents:
    if token in type_literals:
        outp.append(type_literals.index(token))
    elif token in instructions:
        outp.append(instructions.index(token))
    elif token.endswith(":"):
        labels[token[:-1]] = len(outp)
    else:
        success = False
        for i, lit in enumerate(type_literals):
            if token.endswith(lit):
                print(token[:-len(lit)])
                outp.extend(struct.pack(struct_literals[i][0], struct_literals[i][1](token[:-len(lit)])))
                success = True
        if not success: 
            to_replace.append((len(outp), token))
            outp.extend(b"\x00\x00\x00\x00")

for p in to_replace:
    value = -1
    if not (p[1] in labels):
        x = p[1].split("->")
        if len(x) != 2 or not (x[1] in labels) or not (x[0] in labels):
            raise Exception("Cannot resolve token: " + p[1])
        
        b = labels[x[1]]
        a = labels[x[0]]
        value = b - a
    else:
        value = labels[p[1]]
    
    print (f"{p[1]} resolves to {value}")
    override = struct.pack('i', value)
    for i in range(4):
        outp[p[0] + i] = override[i]

with open(args.o, 'wb') as fp:
    fp.write(outp)
