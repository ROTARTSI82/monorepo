#include "cel/eng/load_ini.hpp"

#include <fstream>

#include <stdexcept>
#include <exception>
#include <stack>

#include <cctype>

#include "cel/eng/log.hpp"
#include "cel/eng/misc_util.hpp"

namespace cel {

    struct ini_load_ctx {
        std::string contents;
        std::size_t ind;
        std::size_t end;

        std::string prefix;

        std::unordered_map<std::string, std::string> *store;
    };

    static inline void skip_whitespace(ini_load_ctx &ctx) {
        while (ctx.ind != ctx.end) {
            if (!isspace(ctx.contents.at(ctx.ind))) { return; }
            ctx.ind++;
        }
    }

    static inline void assign_key_value(ini_load_ctx &ctx) {
        std::size_t start = ctx.ind;
        while (ctx.ind != ctx.end) {
            const char c = ctx.contents.at(ctx.ind);
            if (c == '=' || isspace(c)) { break; }
            ctx.ind++;
        }

        std::string key = (ctx.prefix.empty() ? "" : ctx.prefix + ".") + ctx.contents.substr(start, ctx.ind - start); // bounds check?
        skip_whitespace(ctx);
        if (ctx.ind == ctx.end || ctx.contents.at(ctx.ind) != '=') {
            CEL_ERROR("Expected '=' at index {}", ctx.ind);
            throw std::runtime_error{"INI syntax error"};
        }

        ctx.ind++;
        if (ctx.ind != ctx.end && isspace(ctx.contents.at(ctx.ind))) ctx.ind++; // skip the FIRST (and ONLY first) whitespace character after = if it exists.
        std::string value = "";

        bool escaped = false;

        while (ctx.ind != ctx.end) {
            char c = ctx.contents.at(ctx.ind);

            if (escaped) {
                switch (c) {
                case '\n': // line continuation
                case 'n':
                    value += '\n';
                    break;
                case 't':
                    value += '\t';
                    break;
                case '\\':
                    value += '\\';
                    break;
                default:
                    CEL_ERROR("Invalid escape sequence \\{} at index {}", c, ctx.ind);
                    throw std::runtime_error{"INI syntax error"};
                }
                escaped = false;

                ctx.ind++;
                continue;
            }

            if (c == '\n') { 
                ctx.ind++; 
                break; 
            } else if (c == '\\') { 
                escaped = true; 
                ctx.ind++; 
                continue; 
            }

            value += c;

            ctx.ind++;
        }

        // CEL_TRACE("{}={}", key, value);

        if (ctx.store->count(key)) CEL_WARN("Overriding existing key {}", key);
        ctx.store->operator[](key) = value;
    }

    static inline void handle_section(ini_load_ctx &ctx) {
        ctx.ind++;
        if (ctx.ind == ctx.end) {
            CEL_ERROR("Unexpected EOF after section opening '[' at index {}", ctx.ind);
            throw std::runtime_error{"INI syntax error"};
        }

        std::size_t start = ctx.ind;
        while (ctx.ind != ctx.end) {
            if (ctx.contents.at(ctx.ind) == ']') break;
            ctx.ind++;
        }

        std::string scope = ctx.contents.substr(start, ctx.ind - start);
        ctx.prefix = !scope.empty() && scope.at(0) == '.' ? ctx.prefix + scope : scope;
        // CEL_TRACE("New scope {}, prefix={}", scope, ctx.prefix);

        ctx.ind = ctx.ind == ctx.end ? ctx.ind : ctx.ind + 1;
    }

    void load_ini(const std::string &fp, std::unordered_map<std::string, std::string> &store) {
        ini_load_ctx ctx;
        ctx.contents = read_entire_file(fp);
        ctx.ind = 0;
        ctx.end = ctx.contents.size();
        ctx.store = &store;

        skip_whitespace(ctx);
        while (ctx.ind != ctx.end) {
            char c = ctx.contents.at(ctx.ind);
            switch (c) {
            case '#':
                ctx.ind++;
                while (ctx.ind != ctx.end) {
                    c = ctx.contents.at(ctx.ind++);
                    if (c == '\n') break; // returns ctx.ind pointing to the char after \n
                }
                break;
            case '[': // create scope
                handle_section(ctx);
                break;
            case '{': // adopt scope
            case '}': // close scope
            default:
                assign_key_value(ctx);
                break;
            }

            // ctx.ind++;

            skip_whitespace(ctx);
        }
    }
}
