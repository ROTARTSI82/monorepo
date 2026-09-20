#include <iostream>

#include "vm.hpp"
#include "ins_nodes.hpp"

int main() {
    VM vm;

    vm.op_stack.emplace_back(std::make_unique<ValueNode>(std::make_shared<CNumber<double>>(8)));

    OpCode ins[100];
    vm.pc = (uint8_t *) ins;

    // todo: 

/*

op stack: [arg to factorial]

dup
new_int
1

le
jmp_if RETURN_1


RETURN_1:


*/

    vm.exec();

    std::cout << vm.op_stack.back()->evaluate(&vm)->to_string(&vm);
    std::cout << '\n';
}

