#include "cel/app.hpp"

#include "cel/eng/log.hpp"

namespace cel {
    static void glfw_error_callback(int code, const char* description) {
        CEL_ERROR("GLFW ERROR {}: {}", code, description);
    }

    void init() {
        glfwSetErrorCallback(glfw_error_callback);
        CEL_EXITIF(!glfwInit(), 1, "GLFW Initialization failed!");

        CEL_INFO("GLFW compiled {}.{}.{}, linked {}. Timer hz = {}\n", GLFW_VERSION_MAJOR, 
                 GLFW_VERSION_MINOR, GLFW_VERSION_REVISION, glfwGetVersionString(), glfwGetTimerFrequency());
    }

    void quit() {
        glfwTerminate();
    }

    app::app(int argc, char **argv) : settings(argc, argv), ctl_panel(&settings, window_types::control) {
    }

    app::~app() {}

    void app::run() {

        uint64_t frame = 0;


        while (ctl_panel.running()) {
            ctl_panel.next();
            frame++;
            glfwPollEvents();
        }
    }
}