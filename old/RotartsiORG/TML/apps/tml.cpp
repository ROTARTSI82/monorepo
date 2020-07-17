//
// Created by grant on 7/16/20.
//

#include "tml/turing_machine.hpp"
#include <iostream>

int main() {
    tml::TuringMachine machine;
    machine.seek(0); // head 0
    machine.setState(0); // state 0
    std::cout << std::to_string(machine.loopExecute()) << std::endl;
}
