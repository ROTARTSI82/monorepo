#include <iostream>

#include "vm.hpp"
#include "ins_nodes.hpp"

int main() {
    VM vm;

    vm.op_stack.emplace_back(std::make_unique<ValueNode>(std::make_shared<CNumber<uint64_t>>(2)));
    vm.op_stack.emplace_back(std::make_unique<ValueNode>(std::make_shared<CNumber<double>>(8)));

    OpCode ins[] = {OpCode::LE, OpCode::ABORT};
    vm.pc = (uint8_t *) ins;

    vm.exec();

    std::cout << vm.op_stack.back()->evaluate(&vm)->to_string(&vm);
    std::cout << '\n';
}

