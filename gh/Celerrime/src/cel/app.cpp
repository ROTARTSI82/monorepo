#include "cel/app.hpp"

#include "cel/eng/log.hpp"
#include "cel/eng/misc_util.hpp"
#include "cel/eng/gl.hpp"

#include <fmt/format.h>
#include <chrono>

#include "cel/game/mandelbrot_layer.hpp"

namespace cel {
    static void glfw_error_callback(int code, const char* description) {
        CEL_ERROR("GLFW ERROR {}: {}", code, description);
    }

    void init() {
        glfwSetErrorCallback(glfw_error_callback);
        CEL_THROWIF(!glfwInit(), "GLFW Initialization failed!", "GLFW Initialization failed!");

        CEL_INFO("GLFW compiled {}.{}.{}, linked {}. Timer hz = {}", GLFW_VERSION_MAJOR, 
                 GLFW_VERSION_MINOR, GLFW_VERSION_REVISION, glfwGetVersionString(), glfwGetTimerFrequency());
    }

    void quit() {
        CEL_TRACE("glfwTerminate()");
        glfwTerminate();
    }

    static void run_logic(app &parent) {
        CEL_TRACE("Enter run logic");
        while (parent.logic_running) {
            parent.logic_timer.tick();

            {
                std::unique_lock lg(parent.frames_due_mtx);
                parent.logic_convar.wait(lg, [&]() -> bool { return parent.logic_frames_due > 0; });
                parent.logic_frames_due--;
            }

            {
                std::lock_guard lg(parent.frame_mtx);
                parent.world.tick(parent.win.input_for(parent.frame_no));
                parent.frame_no++;
            }
        }

        CEL_TRACE("Exit run logic");
    }


    app::app(int argc, char **argv) : settings(argc, argv), win(&settings), menus(this), world(this), logic_thread(run_logic, std::ref(*this)) {
        
    }

    app::~app() {
        CEL_TRACE("~app()");

        logic_running = false;
        logic_frames_due = 10;
        logic_convar.notify_all();

        if (logic_thread.joinable()) logic_thread.join();
        else logic_thread.detach();
    }

    void app::run() {
        float target_fps = 60.0f;
        float logic_target_fps = 60.0f;
        bool paused = false;
        logic_frames_due = std::numeric_limits<uint64_t>::max();
        logic_convar.notify_one();

        world.layers.emplace_back(world.get_layer<game::mandelbrot_layer>());
        
        while (win.running()) {
            render_timer.tick();

            int w, h;
            glfwGetFramebufferSize(win.get_handle(), &w, &h);
            glViewport(0, 0, w, h);
            
            glClearColor(1, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            menus.new_frame();

            ImGui::Begin("Debug app::run()");
            ImGui::Text("io.WantCaptureKeyboard = %i, io.WantCaptureMouse = %i", ImGui::GetIO().WantCaptureKeyboard, ImGui::GetIO().WantCaptureMouse);
            ImGui::Text("Frame: %lu", frame_no);
            ImGui::SameLine();
            paused = !logic_frames_due;
            if (ImGui::Checkbox("Logic Paused", &paused)) {
                logic_frames_due = paused ? 0 : std::numeric_limits<uint64_t>::max();
                logic_convar.notify_one();
            }

            ImGui::Text("Render Thread FPS: %f", render_timer.get_fps());
            ImGui::Text("Logic Thread FPS: %f", logic_timer.get_fps());

            if (ImGui::SliderFloat("fps", &target_fps, 1.0f, 100.0f, "Target FPS (Render): %f")) {
                render_timer.set_target_fps(target_fps);
            }
            if (ImGui::SliderFloat("fps##logic", &logic_target_fps, 1.0f, 100.0f, "Target FPS (Logic): %f")) {
                logic_timer.set_target_fps(logic_target_fps);
            }

            uint64_t min = 0, max = 60;
            ImGui::SliderScalar("frames", ImGuiDataType_U64, &logic_frames_due, &min, &max, "Frames Due: %i");
            if (ImGui::Button("Notify Condition Variable")) logic_convar.notify_one();

            ImGui::Separator();

            ImGui::Text("Layers");
            for (const auto &l : world.layers) {
                ImGui::Text("%s", typeid(*l.get()).name());
            }

            if (ImGui::Button("Pop Layer")) world.layers.pop_back();
            ImGui::SameLine();
            if (ImGui::Button("Push Layer")) { 
                world.clear_layer_cache(); // prevent get_layer() from returning a pointer to the same layer
                world.layers.emplace_back(world.get_layer<game::mandelbrot_layer>());
            }

            ImGui::End();

            {
                std::lock_guard lg(frame_mtx);
                world.draw();
            }

            menus.draw();

            glfwSwapBuffers(win.get_handle());
            glfwPollEvents();
            flush_gl_errors();
        }
    }
}