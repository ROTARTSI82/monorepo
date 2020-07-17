//
// Created by grant on 7/17/20.
//

#pragma once

#ifndef TML_PARSER_HPP
#define TML_PARSER_HPP

#include <vector>
#include <string>

#include "tml/turing_machine.hpp"

namespace tml {
    int compileFile(const std::string &filename, tml::TuringMachine *machine);
}

#endif //TML_PARSER_HPP
