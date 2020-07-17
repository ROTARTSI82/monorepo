//
// Created by grant on 7/16/20.
//

#include "tml/parser.hpp"
#include "tml/turing_machine.hpp"
#include <iostream>

int main() {
    tml::TuringMachine machine;
    tml::compileFile("./res/sample.tml", &machine);

//    tml::TuringMachine machine;
//    machine.seek(0); // head 0
//    machine.setState(0); // state 0
//    std::cout << std::to_string(machine.loopExecute()) << std::endl;
}
