//
// Created by grant on 7/16/20.
//

#pragma once

#ifndef LIBTML_TURING_MACHINE_HPP
#define LIBTML_TURING_MACHINE_HPP

#include <unordered_map>
#include <vector>
#include <functional>

namespace tml {
    void emitWarning(const std::string &msg);

    void emitError(const std::string &msg);

    void emitVerbose(const std::string &msg);

    typedef long State;
    typedef long Symbol;

    constexpr Symbol BLANK_SYMBOL = 0;
    constexpr Symbol ILLEGAL_SYMBOL = -1;

    constexpr State START_STATE = 0;
    constexpr State HALT_STATE = -1;
    constexpr State NOP_STATE = -2;

    struct StateAndSymbolPair {
        StateAndSymbolPair(State inState, Symbol inSym);

        State state;
        Symbol symbol;

        bool operator==(const StateAndSymbolPair &rhs) const;
    };
}

namespace std {
    template <>
    struct hash<tml::StateAndSymbolPair> {
        std::size_t operator()(const tml::StateAndSymbolPair& k) const {
            // boost hash combine https://www.boost.org/doc/libs/1_73_0/boost/container_hash/hash.hpp]
            auto lhs = static_cast<unsigned long>(k.symbol);
            auto rhs = static_cast<unsigned long>(k.state);
            return lhs ^ (rhs + 0x9e3779b9 + (lhs << 6UL) + (lhs >> 2UL));
        }
    };
}

namespace tml {

    enum MoveOperation {
        eLeft = -1,
        eRight = 1,
        eNop = NOP_STATE // have these be equal so NOP can resolve to 1 value
    };

    struct Instruction {
        Symbol toWrite;
        MoveOperation moveOp;
        State newState;

        bool operator==(const Instruction &rhs) const;
    };

    class TuringMachine {
    private:
        State stateRegister{};

        std::unordered_map<StateAndSymbolPair, Instruction> instructionTable;

        std::unordered_map<long, Symbol> tape;

        long head{};

    public:
        TuringMachine() = default;
        virtual ~TuringMachine() = default;

        TuringMachine(const TuringMachine &rhs) noexcept;
        TuringMachine &operator=(const TuringMachine &rhs) noexcept;

        TuringMachine(TuringMachine &&rhs) noexcept;
        TuringMachine &operator=(TuringMachine &&rhs) noexcept;

        bool operator==(const TuringMachine &rhs) const;
        bool operator!=(const TuringMachine &rhs) const;

        inline void insertInstruction(const StateAndSymbolPair &ssp, const Instruction &instruction) {
            instructionTable[ssp] = instruction;
        }

        inline void seek(size_t newHead) {
            head = newHead;
        }

        inline void setTape(long index, Symbol sym) {
            tape[index] = sym;
        }

        inline bool tapeIndexPresent(long index) {
            return tape.find(index) != tape.end();
        }

        inline bool instructionPresent(const StateAndSymbolPair &ssp) {
            return instructionTable.find(ssp) != instructionTable.end();
        }

        inline void setState(State s) {
            stateRegister = s;
        }

        void executeInstruction(const Instruction &instruction);

        int loopExecute(const std::function<void(TuringMachine *, const Instruction &)>& cb = [](TuringMachine *p,
                const Instruction &s) { p->executeInstruction(s); });
    };
}

#endif //LIBTML_TURING_MACHINE_HPP
