#include "ttt.hpp"
#include <iostream>
#include <unordered_set>

struct Position {
public:
    std::bitset<9> x;
    std::bitset<9> o;
    bool move;

    Position() = default;
    Position(uint16_t x, uint16_t o, bool m) : x(x), o(o), move(m) {}

    inline uint64_t get() const {
        return x.to_ullong() | (o.to_ullong() << 20); // | ((move ? 1ULL : 0ULL) << 40);
    }

    inline Position trans(const std::function<int(int)> &f) const {
        Position ret{};

        for (int i = 0; i < 9; i++) {
            int dest = f(i);
            ret.x[dest] = x[i];
            ret.o[dest] = o[i];
        }

        ret.move = move;
        return ret;
    }

    inline Position rot90() const {
        return trans([](int i) { return ((i + 1) * 3) % 10 - 1; });
    }

    inline Position rot180() const {
        return rot90().rot90();
    }

    inline Position rot270() const {
        return rot180().rot90();
    }

    inline Position vert_flip() const {
        return trans([](int i) { return i < 3 ? i + 6 : i > 5 ? i - 6 : i; });
    }

    inline Position major_flip() const {
        return rot90().vert_flip();
    }

    inline Position minor_flip() const {
        return vert_flip().rot90();
    }

    inline Position horiz_flip() const {
        return major_flip().rot270();
//        return major_flip().rot270().vert_flip();
    }
};

int main() {
    TicTacToe game;

    std::unordered_set<uint64_t> unique;

    std::function<void(void)> run = [&]() {
        std::unordered_set<uint64_t> positions;
        std::function<void(const Position &p)> search = [&](const Position &p) {
            uint64_t v = p.get();
            if (positions.count(v) > 0)
                return;

            positions.insert(v);
            search(p.rot90());
            search(p.vert_flip());
        };

        Position pos{game.x, game.o, game.is_x_turn};
        search(pos);

//        positions.insert(pos.get());
//        positions.insert(pos.rot90().get());
//        positions.insert(pos.rot180().get());
//        positions.insert(pos.rot270().get());
//        positions.insert(pos.vert_flip().get());
//        positions.insert(pos.horiz_flip().get());
//        positions.insert(pos.major_flip().get());
//        positions.insert(pos.minor_flip().get());

        if (std::none_of(positions.begin(), positions.end(), [&](uint64_t v) { return unique.count(v) > 0; })) {
            unique.insert(pos.get());
            game.print();
            std::cout << '\n';
        }

        if (game.state != T3State::ONGOING) return;
        for (int i = 0; i < 9; i++) {
            if (!game.is_legal(i)) continue;

            game.make_move(i);
            run();
            game.unmake_move(i);
        }
    };

    run();

    std::cout << unique.size() << '\n';
}

