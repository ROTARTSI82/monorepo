#include "ttt.hpp"
#include <iostream>
#include <unordered_set>

struct Position {
    std::bitset<9> x;
    std::bitset<9> o;
    bool move;

    Position(uint16_t x, uint16_t o, bool m) : x(x), o(o), move(m) {}


};

int main() {
    TicTacToe game;

    std::unordered_set<uint64_t> positions;

    auto run = [&]() {
        if (game.state != T3State::ONGOING) return;

        // game.gen_best_moves();
        uint64_t bb = game.x | (static_cast<uint64_t>(game.o) << 16) | (game.is_x_turn ? 1 << 32 : 0);

        if (positions.count(rot) )


        for (int i = 0; i < 9; i++) {
            if (!game.is_legal(move)) continue;

            game.make_move(move);
            run();
            game.unmake_move(move);
        }
    };

    run();

    std::cout << positions.size() << '\n';
    for (auto &i : positions) {

    }

}

