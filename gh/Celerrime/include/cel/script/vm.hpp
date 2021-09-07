/**
 * @file vm.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-28
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_SCRIPT_VM_HPP
#define CEL_SCRIPT_VM_HPP

#include <cstdint>
#include <cstddef>
#include <vector>
#include <stack>
#include <functional>
#include <cstring>

namespace cel::scr {
    enum class type_t : uint8_t {
        u8, u16, u32, u64,
        i8, i16, i32, i64,
        f32, f64,
        size,
    };

    enum class cfunc_index : uint8_t {
        malloc, realloc, free, memcpy, memset, memcmp,
        memmove,

        fopen, fread, fwrite, fclose, fflush, 
        fgetpos, fsetpos, fseek, ftell,

        // stdin and stdout funcs here. printf, scanf, puts, gets, etc.

        // string functions here. strlen, strcpy, strcmp, etc.

        socket, bind, listen, connect, accept, send, recv, sendto, recvfrom, /* write, read, */
        close, gethostbyname, gethostbyaddr, select, poll, getsockopt, setsockopt
    };

    // all jumps are relative to the next instruction after jmp. (i.e. jmp 0 == nop)
    enum class opcode_t : uint8_t {
        panic, nop,
        push8, push16, push32, push64, // push bytes literally.
        pop, pop32, ind_pop, // pop expects u16 arg, pop32 expects u32. ind_pop expects a u32 on the top of the op stack.
        ind_jmp, ind_jmpif, // indirect jumps, no args. all relative. op stack: {bot, [addr s32], [bool u8]?, top}
        ind_jabs, ind_jabsif, // indirect absolute jumps (absolute means pointer!) op stack same as rel, but addr is u64.
        ind_call, ind_cabs, // No args. On op stack: {bot, [addr s32/u64], args..., [bytes to cpy u16], top}
        jmp, jmpif, jabs, jabsif, call, cabs, // s32 for relative, u64 for absolute. conditional variants expect uint8_t on top of op stack.
        ret, // pops a frame
        get_lbp, get_lsp, // Base Pointer, Stack Pointer. WARNING: Values returned by these can get invalidated after `mod_lsp`
        dref_lbp, dref_lsp, // args: [type] [offset u32]
        set_lbp, set_lsp, // set the value at a u16/32 offset. Args: [type] [offset]
        dref_gbp, set_gbp, // [type] [offset]
        get_gbp, get_gsize, // returns the size of the globals stack in u64
        mod_gsp32, // this instruction shouldn't really be used.
        mod_lsp, mod_lsp32, // push to or pop from the locals stack. Expects a s16 or s32 respectively
        get_pc, // get program counter. points to the NEXT instruction AFTER get_pc
        dref, // [ptr type]
        set_ptr, // [ptr type], takes op stack {bottom [value] [ptr] top}
        conv, // [type from] -> [type to]
        add, sub, mul, div, mod, // modulo % lhs is below rhs. all take [type]
        bit_xor, bit_and, bit_or, bit_compl_not, // compl_not = ~. all take [type]
        bit_shl, bit_shr,
        bool_xor, bool_and, bool_or, bool_not, // takes [type]
        ne, eq, lt, gt, le, ge, // not equal, equals, less than, greater than, less than or equal to, greater than or equal to. all take [type]

        ind_cfunc, cfunc,
        clear_locals, clear_globals, clear_ops, 

        EXT_DBG_outp, // output the top value of the operand stack literally. takes [type]
        size
    };

    struct frame {
        uint8_t *ret_addr;
        size_t lbp; // stored as an index into locals in case std::vector reallocates it.
        frame(uint8_t *r, size_t l) : ret_addr(r), lbp(l) {};
    };

    class vm {
    public:
        std::vector<uint8_t> globals;
    };

    class thread {
    private:

        inline void new_frame(uint16_t cpy_bytes, uint8_t pc_fwrd = 0) {
            auto size = locals.size();
            frames.emplace_back(pc + pc_fwrd, size);
            locals.resize(size + cpy_bytes);
            memcpy(&locals.at(size), &operands.at(operands.size() - cpy_bytes), cpy_bytes); // use std::copy?
            operands.resize(operands.size() - cpy_bytes);
        }

    public:
        vm *parent;
        uint8_t *pc; // program counter
        std::vector<uint8_t> operands, locals;
        std::vector<frame> frames;

        template <typename T>
        inline T read_pc() {
            T ret = *reinterpret_cast<T *>(pc);
            pc += sizeof(T);
            return ret;
        }

        template <typename T, typename B>
        inline void push_op(const B *v) {
            auto s = operands.size();
            operands.resize(s + sizeof(T));
            *reinterpret_cast<T *>(&operands.at(s)) = *reinterpret_cast<const T *>(v);
        }

        template <typename T>
        inline void push_op_val(const T v) {
            auto s = operands.size();
            operands.resize(s + sizeof(T));
            *reinterpret_cast<T *>(&operands.at(s)) = *reinterpret_cast<const T *>(&v);
        }

        template <typename T>
        inline T pop_op() {
            auto ind = operands.size() - sizeof(T);
            T ret = *reinterpret_cast<T *>(&operands.at(ind));
            operands.resize(ind);
            return ret;
        }

        // returns true if we are still running.
        bool single_cycle();
    };

    class bytecode_builder {
    public:
        std::vector<uint8_t> code;

        template <typename T>
        void add(T val) {
            auto s = code.size();
            code.resize(s + sizeof(T));
            *reinterpret_cast<T *>(&code.at(s)) = val;
        }
    };
}

#endif