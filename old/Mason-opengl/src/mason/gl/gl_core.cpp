//
// Created by Grant on 9/26/19.
//

#include "mason/gl/gl_core.h"

#ifdef MASON_ENABLE_IMGUI
#define IMGUI_IMPL_OPENGL_LOADER_GLEW

#include "imgui/imgui.cpp"
#include "imgui/imgui_impl_glfw.cpp"
#include "imgui/imgui_impl_opengl2.cpp"
#include "imgui/imgui_draw.cpp"
#include "imgui/imgui_widgets.cpp"
#include "imgui/imgui_demo.cpp"

#endif

namespace mason::gl {
    void (*quit)() = &glfwTerminate;

    static void on_glfw_err(int errcode, const char *msg) {
        MASON_WARN("[** GLFW ERROR {} **]: {}", errcode, msg);
    }

    void init_glew(bool use_exp) {

        // GLEW init code

        glewExperimental = use_exp ? GL_TRUE : GL_FALSE;

        GLenum state = glewInit();

        if (state != GLEW_OK) {
            MASON_CRITICAL("Failed to init GLEW! This WILL crash!");
        }

        MASON_INFO("Successfully initialized GLEW {} (glewExperimental={})", glewGetString(GLEW_VERSION),
                   glewExperimental);

        // General OpenGL code

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendEquation(GL_ADD);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        glEnable(GL_STENCIL_TEST);
        glStencilMask(0x00);  // Everything gets written as-is
        glStencilFunc(GL_ALWAYS, 0, 0x00);  // Everything passes
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);  // Keep everything

        MASON_INFO("Successfully initialized OpenGL {}", glGetString(GL_VERSION));
        MASON_INFO("OpenGL Vendor: {}", glGetString(GL_VENDOR));
        MASON_INFO("GL Shading Language Version: ", glGetString(GL_SHADING_LANGUAGE_VERSION));
        MASON_INFO("OpenGL Extensions: {}", glGetString(GL_EXTENSIONS));
    }

    void init_glfw() {
        // GLFW init code
        if (!glfwInit()) {
            MASON_CRITICAL("Failed to init GLFW! This WILL crash!");
        }

        glfwSetErrorCallback(on_glfw_err);

        MASON_INFO("Successfully initialized GLFW {}", glfwGetVersionString());
    }

    void handle_single_error(const std::string &msg) {
        GLenum err = glGetError();
        if (err != GL_NO_ERROR) {
            MASON_WARN("[** OPENGL ERROR {} **]: {}", err, msg);
        }
    }

    void flush_errors(const std::string &msg) {
        GLenum err = glGetError();
        while (err != GL_NO_ERROR) {
            MASON_WARN("[** OPENGL ERROR {} **]: {}", err, msg);
            err = glGetError();
        }
    }

#ifdef MASON_ENABLE_IMGUI

    void quit_imgui() {
        // Cleanup
        ImGui_ImplOpenGL2_Shutdown();
        ImGui_ImplGlfw_Shutdown();
        ImGui::DestroyContext();
    }

#else
    void quit_imgui() {};
#endif

}
