#include "cel/script/vm.hpp"
#include "cel/eng/log.hpp"

#include <typeinfo>

#pragma GCC diagnostic ignored "-Wint-to-pointer-cast"

#define CEL_TYPE_BRANCH_CASE(func, enum_type, type_type, ...) case type_t::enum_type: func<type_type>(__VA_ARGS__); break;

#define CEL_BRANCH_ON_TYPE(f) type = *pc++; \
    switch (static_cast<type_t>(type)) { \
    CEL_TYPE_BRANCH_CASE(f, u8, uint8_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, u16, uint16_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, u32, uint32_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, u64, uint64_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i8, int8_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i16, int16_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i32, int32_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i64, int64_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, f32, float, *this) \
    CEL_TYPE_BRANCH_CASE(f, f64, double, *this) \
    default: throw std::runtime_error{"Invalid type enum!"}; \
    }

#define CEL_BRANCH_ON_TYPE_ONLY_INT(f) type = *pc++; \
    switch (static_cast<type_t>(type)) { \
    CEL_TYPE_BRANCH_CASE(f, u8, uint8_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, u16, uint16_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, u32, uint32_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, u64, uint64_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i8, int8_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i16, int16_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i32, int32_t, *this) \
    CEL_TYPE_BRANCH_CASE(f, i64, int64_t, *this) \
    default: throw std::runtime_error{"Invalid type enum!"}; \
    }

#define CEL_BRANCH_ON_TYPE_ARG(f, t, ...) type = *(t).pc++; \
    switch (static_cast<type_t>(type)) { \
    CEL_TYPE_BRANCH_CASE(f, u8, uint8_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, u16, uint16_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, u32, uint32_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, u64, uint64_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i8, int8_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i16, int16_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i32, int32_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i64, int64_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, f32, float, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, f64, double, __VA_ARGS__) \
    default: throw std::runtime_error{"Invalid type enum!"}; \
    }

#define CEL_BRANCH_ON_TYPE_ARG_ONLY_INT(f, t, ...) type = *(t).pc++; \
    switch (static_cast<type_t>(type)) { \
    CEL_TYPE_BRANCH_CASE(f, u8, uint8_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, u16, uint16_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, u32, uint32_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, u64, uint64_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i8, int8_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i16, int16_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i32, int32_t, __VA_ARGS__) \
    CEL_TYPE_BRANCH_CASE(f, i64, int64_t, __VA_ARGS__) \
    default: throw std::runtime_error{"Invalid type enum!"}; \
    }

#define CEL_SIMPLE_OP(op, name) template <typename T> \
    static void name(thread &t) { \
        *reinterpret_cast<T *>(&t.operands.at(t.operands.size() - 2 * sizeof(T))) op \
        *reinterpret_cast<T *>(&t.operands.at(t.operands.size() - sizeof(T))); \
        t.operands.resize(t.operands.size() - sizeof(T)); \
    }

#define CEL_BOOL_OP(op, name) template <typename T> \
    static void name(thread &t) { \
        T rhs = t.pop_op<T>(); \
        T lhs = t.pop_op<T>(); \
        t.push_op_val<uint8_t>((lhs != 0) op (rhs != 0)); \
    }


#define CEL_CMP_OP(op, name) template <typename T> \
    static void name(thread &t) { \
        T rhs = t.pop_op<T>(); \
        T lhs = t.pop_op<T>(); \
        t.push_op_val<uint8_t>(lhs op rhs); \
    }

#define CEL_FORWARD_BRANCH(ins_name) case opcode_t::ins_name: CEL_BRANCH_ON_TYPE(ins_name ## _impl); break;
#define CEL_FORWARD_BRANCH_ONLY_INT(ins_name) case opcode_t::ins_name: CEL_BRANCH_ON_TYPE_ONLY_INT(ins_name ## _impl); break;


namespace cel::scr {

    template <typename T>
    static T pop_op(thread &t) {
        auto ind = t.operands.size() - sizeof(T);
        T ret = *reinterpret_cast<T *>(&t.operands.at(ind));
        t.operands.resize(ind);
        return ret;
    }

    template <typename T>
    inline static void dref_impl(thread &t) {
        t.push_op<T, T>(pop_op<T *>(t));
    }

    template <typename T>
    inline static void set_ptr_impl(thread &t) {
        T *to_set = pop_op<T *>(t);
        *to_set = pop_op<T>(t); // to set is needed because = has right to left associativity.
    }

    template <typename To, typename From>
    inline static void conv_to_impl(From f, thread &t) {
        To val = (To) f; // c-style cast
        t.push_op<To>(&val);
    }

    template <typename T>
    inline static void conv_impl(thread &t) {
        T val = pop_op<T>(t);
        uint8_t type;
        CEL_BRANCH_ON_TYPE_ARG(conv_to_impl, t, val, t);
    }

    template <typename T>
    inline static void EXT_DBG_outp_impl(thread &t) {
        T val = pop_op<T>(t);
        CEL_INFO("EXT_DBG_outp {}: {}", typeid(val).name(), val);
    }

    template <typename T>
    inline static void dref_lbp_impl(thread &t) {
        t.push_op<T>(&t.locals.at(t.frames.back().lbp + t.read_pc<uint32_t>()));
    }

    template <typename T>
    inline static void dref_lsp_impl(thread &t) {
        t.push_op<T>(&t.locals.at(t.locals.size() - t.read_pc<uint32_t>()));
    }

    template <typename T>
    inline static void set_lbp_impl(thread &t) {
        *reinterpret_cast<T *>(&t.locals.at(t.frames.back().lbp + t.read_pc<uint32_t>())) = 
            t.pop_op<T>();
    }

    template <typename T>
    inline static void set_lsp_impl(thread &t) {
        *reinterpret_cast<T *>(&t.locals.at(t.locals.size() - t.read_pc<uint32_t>())) = 
            t.pop_op<T>();
    }

    template <typename T>
    inline static void dref_gbp_impl(thread &t) {
        t.push_op<T>(&t.parent->globals.at(t.read_pc<uint32_t>()));
    } 

    template <typename T>
    inline static void set_gbp_impl(thread &t) {
        *reinterpret_cast<T *>(&t.parent->globals.at(t.read_pc<uint32_t>())) = 
            t.pop_op<T>();
    }

    CEL_SIMPLE_OP(+=, add_impl);
    CEL_SIMPLE_OP(-=, sub_impl);
    CEL_SIMPLE_OP(*=, mul_impl);
    CEL_SIMPLE_OP(/=, div_impl);
    CEL_SIMPLE_OP(%=, mod_impl);
    CEL_SIMPLE_OP(^=, bit_xor_impl);
    CEL_SIMPLE_OP(&=, bit_and_impl);
    CEL_SIMPLE_OP(|=, bit_or_impl);
    CEL_SIMPLE_OP(>>=, bit_shr_impl);
    CEL_SIMPLE_OP(<<=, bit_shl_impl);

    CEL_BOOL_OP(&&, bool_and_impl);
    CEL_BOOL_OP(||, bool_or_impl);
    CEL_BOOL_OP(^, bool_xor_impl);

    CEL_CMP_OP(>, gt_impl);
    CEL_CMP_OP(<, lt_impl);
    CEL_CMP_OP(>=, ge_impl);
    CEL_CMP_OP(<=, le_impl);
    CEL_CMP_OP(==, eq_impl);
    CEL_CMP_OP(!=, ne_impl);

    template <typename T>
    static void bool_not_impl(thread &t) {
        t.push_op_val<bool>(!t.pop_op<T>());
    }

    template <typename T>
    static void bit_compl_not_impl(thread &t) {
        T *operand = reinterpret_cast<T *>(&t.operands.at(t.operands.size() - sizeof(T)));
        *operand = ~(*operand);
    }

    static_assert(sizeof(uint8_t *) == sizeof(uint64_t));

    bool thread::single_cycle() {
        opcode_t ins = static_cast<opcode_t>(*pc++);
        uint8_t type;

        union {
            uint16_t u16;
            uint32_t u32;
            uint8_t *ptr;
            int32_t s32;
        } regs;

        switch (ins) {
        case opcode_t::panic:
            frames.clear(); // signal to stop!
            throw std::runtime_error{"PANIC!"};
        case opcode_t::nop:
            break;
        case opcode_t::push8:
            operands.push_back(*pc++);
            break;
        case opcode_t::push16:
            push_op<uint16_t>(pc);
            pc += 2;
            break;
        case opcode_t::push32:
            push_op<uint32_t>(pc);
            pc += 4;
            break;
        case opcode_t::push64:
            push_op<uint64_t>(pc);
            pc += 8;
            break;
        case opcode_t::pop:
            regs.u16 = read_pc<uint16_t>();
            operands.resize(operands.size() - regs.u16);
            break;
        case opcode_t::pop32:
            regs.u32 = read_pc<uint32_t>();
            operands.resize(operands.size() - regs.u32);
            break;
        case opcode_t::ind_pop:
            regs.u32 = pop_op<uint32_t>();
            operands.resize(operands.size() - regs.u32);
            break;
        case opcode_t::jmp:
            pc += read_pc<int32_t>(); 
            break;
        case opcode_t::jmpif:
            regs.s32 = read_pc<int32_t>();
            if (pop_op<uint8_t>())
                pc += regs.s32;
            break;
        case opcode_t::jabs:
            pc = read_pc<uint8_t *>();
            break;
        case opcode_t::jabsif:
            regs.ptr = read_pc<uint8_t *>();
            if (pop_op<uint8_t>())
                pc = regs.ptr;
            break;
        case opcode_t::cabs:
            new_frame(read_pc<uint16_t>(), 4);
            pc = read_pc<uint8_t *>();
            break;
        case opcode_t::call:
            new_frame(read_pc<uint16_t>(), 4);
            // regs.s32 = read_pc<int32_t>();
            // CEL_DEBUG("CALL +{}", regs.s32);
            pc += read_pc<int32_t>();
            break;
        case opcode_t::ind_jmp:
            pc += pop_op<int32_t>();
            break;
        case opcode_t::ind_jmpif:
            if (pop_op<uint8_t>()) {
                pc += pop_op<int32_t>();
            } else pop_op<int32_t>();
            break;
        case opcode_t::ind_jabs:
            pc = pop_op<uint8_t *>();
            break;
        case opcode_t::ind_jabsif:
            if (pop_op<uint8_t>()) {
                pc = pop_op<uint8_t *>();
            } else pop_op<uint8_t *>();
            break;
        case opcode_t::ind_call:
            new_frame(pop_op<uint16_t>());
            pc += pop_op<int32_t>();
            break;
        case opcode_t::ind_cabs:
            new_frame(pop_op<uint16_t>());
            pc = pop_op<uint8_t *>();
            break;


        case opcode_t::ret:
            pc = frames.back().ret_addr;
            frames.pop_back();
            break;
        case opcode_t::get_lbp:
            push_op<uint8_t *>(&locals.at(frames.back().lbp));
            break;
        case opcode_t::get_lsp:
            push_op_val<uint8_t *>((&operands.back()) + 1);
            break;
        case opcode_t::mod_lsp:
            locals.resize(locals.size() + pop_op<int16_t>());
            break;
        case opcode_t::mod_lsp32:
            locals.resize(locals.size() + pop_op<int32_t>());
            break;

        case opcode_t::get_pc:
            push_op_val<uint8_t *>(pc);
            break;

        case opcode_t::get_gbp:
            push_op_val<uint8_t *>(parent->globals.data());
            break;
        case opcode_t::get_gsize:
            push_op_val<uint64_t>(parent->globals.size());
            break;
        case opcode_t::mod_gsp32:
            parent->globals.resize(parent->globals.size() + pop_op<int32_t>());
            break;

        case opcode_t::ind_cfunc:
        case opcode_t::cfunc:
            throw std::runtime_error{"Unimplemented opcode!"};

        CEL_FORWARD_BRANCH(dref_lbp);
        CEL_FORWARD_BRANCH(dref_lsp);
        CEL_FORWARD_BRANCH(set_lbp);
        CEL_FORWARD_BRANCH(set_lsp);
        CEL_FORWARD_BRANCH(dref_gbp);
        CEL_FORWARD_BRANCH(set_gbp);
        CEL_FORWARD_BRANCH(dref);
        CEL_FORWARD_BRANCH(set_ptr);
        CEL_FORWARD_BRANCH(conv);
        CEL_FORWARD_BRANCH(EXT_DBG_outp);
        CEL_FORWARD_BRANCH(add);
        CEL_FORWARD_BRANCH(sub);
        CEL_FORWARD_BRANCH(mul);
        CEL_FORWARD_BRANCH(div);
        CEL_FORWARD_BRANCH_ONLY_INT(mod);
        CEL_FORWARD_BRANCH_ONLY_INT(bit_xor);
        CEL_FORWARD_BRANCH_ONLY_INT(bit_and);
        CEL_FORWARD_BRANCH_ONLY_INT(bit_or);
        CEL_FORWARD_BRANCH_ONLY_INT(bit_compl_not);
        CEL_FORWARD_BRANCH_ONLY_INT(bit_shl);
        CEL_FORWARD_BRANCH_ONLY_INT(bit_shr);
        CEL_FORWARD_BRANCH(bool_xor);
        CEL_FORWARD_BRANCH(bool_and);
        CEL_FORWARD_BRANCH(bool_or);
        CEL_FORWARD_BRANCH(bool_not);
        CEL_FORWARD_BRANCH(ne);
        CEL_FORWARD_BRANCH(eq);
        CEL_FORWARD_BRANCH(lt);
        CEL_FORWARD_BRANCH(gt);
        CEL_FORWARD_BRANCH(le);
        CEL_FORWARD_BRANCH(ge);

        default:
            throw std::runtime_error{"Illegal opcode!"};
        }

        return !frames.empty();
    }
}
