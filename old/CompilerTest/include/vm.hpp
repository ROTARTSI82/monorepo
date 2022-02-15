#pragma once

#include <cstdint>
#include <vector>
#include <memory>

#include "object.hpp"



enum class OpCode : uint8_t {
    ABORT, NOP,

    ADD, SUB, MUL, DIV, MOD, POW, 

    BNOT, LNOT, 
    BXOR, BAND, BOR,

    JMP,
    LT, GT, LE, GE,
    JMP_IF,

    CALL,
    RET,

    NEW_DOUBLE, NEW_UNSIGNED, NEW_INT, POP,
    INT_TO_JUMP
};


struct Frame {
    uint8_t *restore;

    Frame(uint8_t *restore) : restore(restore) {}
};

class VM {
public:
    std::vector<std::unique_ptr<ExpressionNode>> op_stack;
    std::vector<Frame> callstack;

    std::vector<Object *> globals;
    
    uint8_t *pc;

    bool running = false;

    inline void push(std::unique_ptr<ExpressionNode> &&ptr) {
        op_stack.emplace_back(std::move(ptr));
    }

    inline std::unique_ptr<ExpressionNode> pop() {
        std::unique_ptr<ExpressionNode> ret = std::move(op_stack.back());
        op_stack.pop_back();
        return ret;
    }

    void exec();
};