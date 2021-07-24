#include "cel/eng/window.hpp"

#include "cel/settings.hpp"
#include "cel/eng/log.hpp"

namespace cel {
    window::window(settings_handler *settings, window_types type) : settings(settings), type(type) {
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, CEL_USE_OGL_DBG_CTX);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // macOS support
        win = glfwCreateWindow(settings->wins[static_cast<uint8_t>(type)].width, settings->wins[static_cast<uint8_t>(type)].height, 
                               settings->strs.window_titles[static_cast<uint8_t>(type)].c_str(), NULL, NULL);
        
        CEL_EXITIF(!win, 1, "Window creation failed for type {}", type);
        glfwMakeContextCurrent(win);


        glewExperimental = GL_TRUE;
        int glew_status = glewInit();
        CEL_EXITIF(glew_status != GLEW_OK, 1, "GLEW init failed for window type {}: {}", static_cast<int>(type), glewGetErrorString(glew_status));
    }

    window::~window() {
        glfwDestroyWindow(win);
    }

    void window::next() {
        glfwSwapBuffers(win);
    }
}