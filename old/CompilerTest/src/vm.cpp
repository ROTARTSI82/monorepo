#include "vm.hpp"

#include "ins_nodes.hpp"

void VM::exec() {
    running = true;
    while (running) {
        switch (static_cast<OpCode>(*pc++)) {
        case OpCode::ABORT:
            running = false;
        case OpCode::NOP:
            break;
        case OpCode::ADD:
            push(std::make_unique<AddNode>(pop(), pop()));
            break;
        case OpCode::SUB:
            push(std::make_unique<SubNode>(pop(), pop()));
            break;
        case OpCode::MUL:
            push(std::make_unique<MulNode>(pop(), pop()));
            break;
        case OpCode::DIV:
            push(std::make_unique<DivNode>(pop(), pop()));
            break;
        case OpCode::CALL:
            callstack.push_back(Frame{pc});
            pc = std::dynamic_pointer_cast<JumpAddr>(pop()->evaluate(this))->mutate(this, pc);
            break;
        case OpCode::RET:
            pc = callstack.back().restore;
            callstack.pop_back();
            break;
        case OpCode::JMP:
            pc = std::dynamic_pointer_cast<JumpAddr>(pop()->evaluate(this))->mutate(this, pc);
            break;
        case OpCode::JMP_IF:
            if (pop()->evaluate(this)->is_true(this))
                pc = std::dynamic_pointer_cast<JumpAddr>(pop()->evaluate(this))->mutate(this, pc);
            else
                pop();
            break;
        case OpCode::GT:
            push(std::make_unique<GTNode>(pop(), pop()));
            break;
        case OpCode::LT:
            push(std::make_unique<LTNode>(pop(), pop()));
            break;
        case OpCode::LE:
            push(std::make_unique<LENode>(pop(), pop()));
            break;
        case OpCode::GE:
            push(std::make_unique<GENode>(pop(), pop()));
            break;
        
        case OpCode::POP:
            pop();
            break;

        case OpCode::NEW_DOUBLE:
            push(std::make_unique<ValueNode>(std::make_shared<CNumber<double>>(*reinterpret_cast<double *>(pc))));
            pc += 8;
            break;
        case OpCode::NEW_UNSIGNED:
            push(std::make_unique<ValueNode>(std::make_shared<CNumber<uint64_t>>(*reinterpret_cast<uint64_t *>(pc))));
            pc += 8;
            break;
        case OpCode::NEW_INT:
            push(std::make_unique<ValueNode>(std::make_shared<CNumber<int64_t>>(*reinterpret_cast<int64_t *>(pc))));
            pc += 8;
            break;
        case OpCode::INT_TO_JUMP:
            push(std::make_unique<ValueNode>(std::make_shared<RelativeJump>(std::dynamic_pointer_cast<GenericCNum>(pop()->evaluate(this))->ival())));
        }
    }
}