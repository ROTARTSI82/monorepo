#include "cel/game/imgui_menus.hpp"

#include <imgui/imgui.h>
#include <imgui/backends/imgui_impl_opengl3.h>
#include <imgui/backends/imgui_impl_glfw.h>

#include "cel/app.hpp"

namespace cel {
    imgui_menu::imgui_menu(app_t *a) : parent(a) {
        // Setup Dear ImGui context
        IMGUI_CHECKVERSION();
        ImGui::CreateContext();
        ImGui::GetIO().ConfigFlags |= ImGuiConfigFlags_DockingEnable | ImGuiConfigFlags_ViewportsEnable; // | ImGuiConfigFlags_NavEnableKeyboard;
        ImGui::GetIO().FontGlobalScale = 1.5;

        ImGui::StyleColorsDark();

        // ImGuiStyle &style = ImGui::GetStyle();
        // if (io.ConfigFlags & ImGuiConfigFlags_ViewportsEnable)
        // {
        //     style.WindowRounding = 0.0f;
        //     style.Colors[ImGuiCol_WindowBg].w = 1.0f;
        // }

        ImGui_ImplGlfw_InitForOpenGL(parent->get_window().get_handle(), true);
        ImGui_ImplOpenGL3_Init("#version 330");

        // ImGui::PushItemWidth(2.0);
    }

    imgui_menu::~imgui_menu() {
        ImGui_ImplOpenGL3_Shutdown();
        ImGui_ImplGlfw_Shutdown();
        ImGui::DestroyContext();
    }

    void imgui_menu::debug_tools() {
        // ImGui::Begin("Debug Tools");
        // ImGui::Text("View matrix:\n%s", to_string(parent->view_mat).c_str());
        // ImGui::Text("Projection matrix:\n%s", to_string(parent->proj_mat).c_str());
        // ImGui::End();
    }

    void imgui_menu::new_frame() {
        ImGui_ImplOpenGL3_NewFrame();
        ImGui_ImplGlfw_NewFrame();
        ImGui::NewFrame();
    }

    void imgui_menu::draw() {

        if (show_demo) ImGui::ShowDemoWindow(&show_demo);
        debug_tools();

        ImGui::Render();
        // update opengl stuff here.
        ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());

        if (ImGui::GetIO().ConfigFlags & ImGuiConfigFlags_ViewportsEnable) {
            GLFWwindow *backup_current_context = glfwGetCurrentContext();
            ImGui::UpdatePlatformWindows();
            ImGui::RenderPlatformWindowsDefault();
            glfwMakeContextCurrent(backup_current_context);
        }
    }
}