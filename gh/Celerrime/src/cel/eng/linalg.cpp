#include "cel/eng/linalg.hpp"

#include <fmt/format.h>
#include "cel/eng/log.hpp"
namespace cel {
    // glm::mat2 mat2_transform(glm::vec2 scale, float theta) {
    //     glm::mat2 ret;

    //     float cos = std::cos(theta);
    //     float sin = std::sin(theta);
    //     ret[0].x = cos * scale.x;
    //     ret[0].y = sin * scale.x;

    //     ret[1].x = -sin * scale.y;
    //     ret[1].y = cos * scale.y;

    //     return ret;
    // }

    // glm::mat4 mat4_transform(const glm::vec3 &translate, glm::vec2 scale, float theta) {
    //     glm::mat4 ret{1.0};

    //     float sin = std::sin(theta);
    //     float cos = std::cos(theta);
    //     ret[0].x = cos * scale.x;
    //     ret[0].y = sin * scale.x;

    //     ret[1].x = -sin * scale.y; // cos(theta + 90deg) = -cos(90 - theta) = -sin(theta)
    //     ret[1].y = cos * scale.y; // sin(theta + 90deg) = sin(90deg - theta) = cos(theta)

    //     ret[3].x = translate.x;
    //     ret[3].y = translate.y;
    //     ret[3].z = translate.z;
    //     return ret;
    // }

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