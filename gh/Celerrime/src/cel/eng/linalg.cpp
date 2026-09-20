#include "cel/eng/linalg.hpp"

#include <fmt/format.h>
#include "cel/eng/log.hpp"
namespace cel {

    std::string to_string(const gl_mat4 &operand) {
        return fmt::format("[{}, {}, {}, {}]", to_string(operand.c0), to_string(operand.c1), to_string(operand.c2), to_string(operand.c3));
    }

    std::string to_string(const vec4 &operand) {
        return fmt::format("[{}, {}, {}, {}]", operand.x, operand.y, operand.z, operand.w);
    }

    std::string to_string(const vec3 &operand) {
        return fmt::format("[{}, {}, {}]", operand.x, operand.y, operand.z);
    }

    std::string to_string(const vec2 &operand) {
        return fmt::format("[{}, {}]", operand.x, operand.y);
    }
}