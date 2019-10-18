//
// Created by Grant on 9/26/19.
//


#include "mason/gl/gl_window.h"

namespace mason::gl {

    gl_window::gl_window(const int &w, const int &h, const char *title) {
        init_glfw();
        win = glfwCreateWindow(w, h, title, nullptr, nullptr);
        if (!win) {
            glfwTerminate();
            MASON_CRITICAL("Failed to create GLFW context! This WILL crash!");
            return;
        }

        bind();

        init_glew();
#ifdef MASON_ENABLE_IMGUI
        init_imgui();
#endif

        glfwSetCursorPosCallback(this->win, mason::gl::event_handler::mouse_motion_handler);
        glfwSetKeyCallback(this->win, mason::gl::event_handler::key_handler);
        glfwSetMouseButtonCallback(this->win, mason::gl::event_handler::mouse_button_handler);
    }

    void gl_window::bind() {
        glfwMakeContextCurrent(win);
    }

    gl_window::~gl_window() {
        glfwDestroyWindow(win);
    }

    void gl_window::clear() {

#ifdef MASON_ENABLE_IMGUI
        // Start the Dear ImGui frame
        ImGui_ImplOpenGL2_NewFrame();
        ImGui_ImplGlfw_NewFrame();
        ImGui::NewFrame();
#endif

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    void gl_window::flip() {
#ifdef MASON_ENABLE_IMGUI
        ImGui::Render();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glUseProgram(0);
//        glBindVertexArray(0);

        ImGui_ImplOpenGL2_RenderDrawData(ImGui::GetDrawData());
#endif

        glfwSwapBuffers(win);
        glfwPollEvents();
    }

    int gl_window::poll_close() {
        return glfwWindowShouldClose(win);
    }

    void gl_window::enable_vsync(int interval) {
        glfwSwapInterval(interval);
    }

#ifdef MASON_ENABLE_IMGUI

    void gl_window::init_imgui() {
        // Setup Dear ImGui context
        IMGUI_CHECKVERSION();
        ImGui::CreateContext();
        ImGuiIO &io = ImGui::GetIO();
        (void) io;
        //io.ConfigFlags |= ImGuiConfigFlags_NavEnableKeyboard;     // Enable Keyboard Controls
        //io.ConfigFlags |= ImGuiConfigFlags_NavEnableGamepad;      // Enable Gamepad Controls

        // Setup Dear ImGui style
        ImGui::StyleColorsDark();
        //ImGui::StyleColorsClassic();

        // Setup Platform/Renderer bindings
        ImGui_ImplGlfw_InitForOpenGL(win, true);
        ImGui_ImplOpenGL2_Init();

        MASON_INFO("Successfully initialized ImGui {}", ImGui::GetVersion());
    }

#else
    void gl_window::init_imgui() {}
#endif

}

