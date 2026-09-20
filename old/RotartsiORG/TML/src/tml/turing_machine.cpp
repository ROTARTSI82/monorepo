//
// Created by grant on 7/16/20.
//

#include "tml/turing_machine.hpp"

#include <iostream>

namespace tml {
    void emitWarning(const std::string &msg) {
        std::cerr << "[TML WARNING]: " << msg << std::endl;
    }

    void emitError(const std::string &msg) {
        std::cerr << "[TML ERROR]: " << msg << std::endl;
    }

    void emitVerbose(const std::string &msg) {
        std::cout << "[TML VERBOSE]: " << msg << std::endl;
    }

    bool StateAndSymbolPair::operator==(const StateAndSymbolPair &rhs) const {
        return this->state == rhs.state && this->symbol == rhs.symbol;
    }

    StateAndSymbolPair::StateAndSymbolPair(State inState, Symbol inSym) : state(inState), symbol(inSym) {}

    TuringMachine::TuringMachine(const TuringMachine &rhs) noexcept {
        *this = rhs;
    }

    TuringMachine &TuringMachine::operator=(const TuringMachine &rhs) noexcept {
        if (this == &rhs || (*this) == rhs) {
            return *this; // self assignment, do nothing ;p
        }

        head = rhs.head;
        stateRegister = rhs.stateRegister;
        tape = rhs.tape;
        instructionTable = rhs.instructionTable;

        return *this;
    }

    TuringMachine::TuringMachine(TuringMachine &&rhs) noexcept {
        *this = std::move(rhs);
    }

    TuringMachine &TuringMachine::operator=(TuringMachine &&rhs) noexcept {
        if (this == &rhs || (*this) == rhs) {
            return *this; // self assignment, do nothing ;p
        }

        head = rhs.head;
        stateRegister = rhs.stateRegister;
        tape = std::move(rhs.tape);
        instructionTable = std::move(rhs.instructionTable);

        return *this;
    }

    bool TuringMachine::operator==(const TuringMachine &rhs) const {
        return head == rhs.head && stateRegister == rhs.stateRegister && tape == rhs.tape &&
            instructionTable == rhs.instructionTable;
    }

    bool TuringMachine::operator!=(const TuringMachine &rhs) const {
        return !((*this) == rhs);
    }

    void TuringMachine::executeInstruction(const Instruction &instruction) {
        tape[head] = instruction.toWrite;

        switch (instruction.moveOp) {
            case (eLeft): head--; break;
            case (eRight): head++; break;
            case (eNop): break;
            default: throw std::runtime_error("Unreachable state: Instruction with invalid moveOp!");
        }

        if (instruction.newState != NOP_STATE) {
            stateRegister = instruction.newState;
        }
    }

    int TuringMachine::loopExecute(const std::function<void(TuringMachine *, const Instruction &)>& cb) {
        while (stateRegister != HALT_STATE) {

            if (tape.find(head) == tape.end()) {
                emitWarning("Head location is out-of-bounds! [head=" + std::to_string(head) + ", -Wbounds]");
                tape[head] = BLANK_SYMBOL; // in the future, a "tape generator" may be introduced instead of blanks.
            }

            if (instructionTable.find(StateAndSymbolPair{stateRegister, tape[head]}) == instructionTable.end()) {
                emitError("Instruction table entry missing for [" + std::to_string(stateRegister) + ", " + std::to_string(tape[head]) + "]!");
                return 1;
            }

            cb(this, instructionTable[StateAndSymbolPair{stateRegister, tape[head]}]);
        }

        return 0;
    }

    bool Instruction::operator==(const Instruction &rhs) const {
        return toWrite == rhs.toWrite && moveOp == rhs.moveOp && newState == rhs.newState;
    }
}
