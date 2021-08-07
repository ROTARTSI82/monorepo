#include "cel/app.hpp"

#include "cel/eng/log.hpp"
#include "cel/eng/misc_util.hpp"
#include "cel/eng/gl.hpp"

namespace cel {
    static void glfw_error_callback(int code, const char* description) {
        CEL_ERROR("GLFW ERROR {}: {}", code, description);
    }

    void init() {
        glfwSetErrorCallback(glfw_error_callback);
        CEL_EXITIF(!glfwInit(), 1, "GLFW Initialization failed!");

        CEL_INFO("GLFW compiled {}.{}.{}, linked {}. Timer hz = {}", GLFW_VERSION_MAJOR, 
                 GLFW_VERSION_MINOR, GLFW_VERSION_REVISION, glfwGetVersionString(), glfwGetTimerFrequency());
    }

    void quit() {
        CEL_TRACE("quit()");
        rectangle_vbo::destroy();
        CEL_TRACE("glfwTerminate()");
        glfwTerminate();
    }

    app::app(int argc, char **argv) : settings(argc, argv), ctl_panel(&settings, window_types::control) {
        shader frag{GL_FRAGMENT_SHADER, read_entire_file("./res/shader/default.frag")};
        shader vert{GL_VERTEX_SHADER, read_entire_file("./res/shader/default.vert")};

        default_shaders.attach(frag.get_id());
        default_shaders.attach(vert.get_id()); 
        default_shaders.link();
    }

    app::~app() {
        CEL_TRACE("~app()");
    }

    void app::run() {

        uint64_t frame = 0;

        while (ctl_panel.running()) {
            ctl_panel.next();
            frame++;
            glfwPollEvents();
        }
    }
}