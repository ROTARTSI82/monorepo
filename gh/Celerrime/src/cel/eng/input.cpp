#include "cel/eng/input.hpp"

#include <memory>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

namespace cel {

    std::string to_string(const input_frame &inp) {
        std::string ret = "{Dir=" + to_string(inp.direction) +", Btns=[";
        ret.reserve(ret.length() + 2 + (int) button_id::size * 7);
        for (const auto &btn : inp.buttons) {
            ret += (btn ? "true" : "false");
            ret += ",";
        }
        ret += "]}";
        return ret;
    }

    hacky_input_provider::hacky_input_provider() = default;
    hacky_input_provider::~hacky_input_provider() = default;

    input_frame hacky_input_provider::next(uint64_t frame, GLFWwindow *win) {
        input_frame ret = {};
        if (glfwGetKey(win, GLFW_KEY_UP) == GLFW_PRESS) ret.direction.y += 1;
        if (glfwGetKey(win, GLFW_KEY_DOWN) == GLFW_PRESS) ret.direction.y -= 1;
        if (glfwGetKey(win, GLFW_KEY_RIGHT) == GLFW_PRESS) ret.direction.x += 1;
        if (glfwGetKey(win, GLFW_KEY_LEFT) == GLFW_PRESS) ret.direction.x -= 1;
        ret.direction.clamp_magn_in_place(1);

        ret.buttons[(int) button_id::zoom_in] = glfwGetKey(win, GLFW_KEY_A) == GLFW_PRESS;
        ret.buttons[(int) button_id::zoom_out] = glfwGetKey(win, GLFW_KEY_S) == GLFW_PRESS;
        return ret;
    }
}