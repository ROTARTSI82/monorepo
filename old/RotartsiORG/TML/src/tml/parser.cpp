//
// Created by grant on 7/17/20.
//

#include <tml/parser.hpp>
#include <fstream>
#include <stack>
#include <iostream>
#include <unordered_map>
#include <sstream>
#include <unordered_set>

namespace tml {

    struct Line {
        unsigned fileLoc;
        std::string value;
    };

    int compileFile(const std::string &filename, tml::TuringMachine *machine) {
        std::ifstream fd(filename);
        if (!fd.is_open()) {
            throw std::runtime_error("Cannot open file " + filename);
        }

        // first pass, parse lines by looking at semicolons and get rid of comments and whitespace
        std::vector<Line> lines;
        lines.emplace_back(Line{1, ""});
        unsigned realLineCounter = 2;
        char c;
        bool hashComment = false;
        while (fd >> std::noskipws >> c) {
            if (c == '\n') {
                realLineCounter++;
            }

            if (hashComment && c != '\n') {
                continue;
            } else if (hashComment && c == '\n') {
                hashComment = false;
                continue;
            }

            if (c == '#') {
                hashComment = true;
                continue;
            }

            if (c == ';') {
                lines.emplace_back(Line{realLineCounter, ""});
                continue;
            }

            if (c != ' ' && c != '\t' && c != '\n') {
                (lines.end() - 1)->value.push_back(c);
            }
        }

        // second pass, load everything
        std::unordered_map<std::string, long> symbolTable = {
                {"NOP", NOP_STATE}, {"BLANK", BLANK_SYMBOL}, {"START", START_STATE}, {"HALT", HALT_STATE},
                {"LEFT", eLeft}, {"RIGHT", eRight}
        };

        auto resolveSymbol = [&](const std::string &sym) -> long {
            if (symbolTable.find(sym) != symbolTable.end()) {
                return symbolTable[sym];
            } else {
                static std::unordered_set<char> legal = {{'-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+'}};

                for (const auto &l : sym) {
                    if (legal.find(l) == legal.end()) {
                        return ILLEGAL_SYMBOL;
                    }
                }

                return std::strtoll(sym.c_str(), nullptr, 10);
            }
        };

        unsigned lineCounterTMP = 1;
        for (const auto &line : lines) {

            std::ostringstream lstrm;
            lstrm << "Line " << lineCounterTMP << " [" << filename << ":" << line.fileLoc << "]: " << line.value;
            lineCounterTMP++;

            emitVerbose(lstrm.str());

            // part 1 load check if line is an alias line and load aliases (aliases to aliases are allowed)
            if (line.value.rfind("addAliases(", 0) == 0 && (*(line.value.end() - 1)) == ')') {

                emitVerbose("\t + this line was recognized as an alias definition!");

                auto rawAliases = line.value.substr(11, line.value.size() - 12);

                size_t pos = 0;
                std::string token;
                while ((pos = rawAliases.find(',')) != std::string::npos) {
                    token = rawAliases.substr(0, pos);

                    auto colonPos = token.find(':');
                    std::string sym = token.substr(0, colonPos);
                    long value = resolveSymbol(token.substr(colonPos + 1, token.size() - colonPos));
                    if (value == ILLEGAL_SYMBOL) {
                        emitError(lstrm.str());
                        emitError("\t [IllegalSymbol] Cannot resolve symbol `" + token.substr(colonPos + 1, token.size() - colonPos) + "`!");
                        return 2;
                    }

                    if (symbolTable.find(sym) != symbolTable.end()) {
                        std::ostringstream stream;
                        stream << "\t [AliasError] Symbol '" << sym <<
                                    "' is a duplicate/conflicting symbol!";

                        emitError(lstrm.str());
                        emitError(stream.str());
                        return 1;
                    } else {
                        symbolTable[sym] = value;
                        emitVerbose("\t\t + Registered alias `" + sym + "` -> " + std::to_string(value));
                    }

                    rawAliases.erase(0, pos + 1);
                }
                auto colonPos = rawAliases.find(':');
                std::string sym = rawAliases.substr(0, colonPos);
                long value = resolveSymbol(rawAliases.substr(colonPos + 1, rawAliases.size() - colonPos));
                if (value == ILLEGAL_SYMBOL) {
                    emitError(lstrm.str());
                    emitError("\t [IllegalSymbol] Cannot resolve symbol `" + rawAliases.substr(colonPos + 1, rawAliases.size() - colonPos) + "`!");
                    return 2;
                }

                if (symbolTable.find(sym) != symbolTable.end()) {
                    std::ostringstream stream;
                    stream << "\t [AliasError] Symbol '" << sym <<
                           "' is a duplicate/conflicting symbol!";

                    emitError(lstrm.str());
                    emitError(stream.str());
                    return 1;
                } else {
                    symbolTable[sym] = value;
                    emitVerbose("\t\t + Registered alias `" + sym + "` -> " + std::to_string(value));
                }
                continue;
            }

            // part 2: check if its a tapeIndexed statement
        }

        return 0;
    }
}

