#include "cel/app.hpp"

#include "cel/eng/log.hpp"
#include "cel/eng/misc_util.hpp"
#include "cel/eng/gl.hpp"

#include <fmt/format.h>
#include <chrono>
#include <imgui/imgui.h>

#include "cel/game/dbg/tgrs.hpp"

namespace cel {
    static void glfw_error_callback(int code, const char* description) {
        CEL_ERROR("GLFW ERROR {}: {}", code, description);
    }

    void init() {
        glfwSetErrorCallback(glfw_error_callback);
        if (!glfwInit()) {
            constexpr const char *msg = "GLFW Initialization failed!";
            CEL_CRITICAL(msg);
            throw std::runtime_error{msg};
        }

        CEL_INFO("GLFW compiled {}.{}.{}, linked {}. Timer hz = {}", GLFW_VERSION_MAJOR, 
                 GLFW_VERSION_MINOR, GLFW_VERSION_REVISION, glfwGetVersionString(), glfwGetTimerFrequency());
    }

    void quit() {
        CEL_TRACE("glfwTerminate()");
        glfwTerminate();
    }

    static void framebuffer_size_callback(GLFWwindow* window, int width, int height) {
        app_t *app = reinterpret_cast<app_t *>(glfwGetWindowUserPointer(window));

        // TODO: should i really be storing this directly into the settings?
        app->opt.win_width = width;
        app->opt.win_height = height;

        if (!app->opt.enforce_aspect) {
            auto resolution = app->opt.fb_size_resolver(width, height);
            app->indirect_target.resize(resolution.x, resolution.y);
            return;
        }

        generic_vec2<GLsizei> resolution;
        if (static_cast<float>(width) / height > app->opt.aspect) {
            // case: width is too long
            float target_width = (height * app->opt.aspect);
            float wover = width / target_width;
            app->ortho_proj = gl_mat4::ortho(-wover, wover, -1, 1, cel::znear, cel::zfar);

            resolution = app->opt.fb_size_resolver((int) target_width, height);
        } else {
            float target_height = (width / app->opt.aspect);
            float hover = height / target_height;
            app->ortho_proj = gl_mat4::ortho(-1, 1, -hover, hover, cel::znear, cel::zfar);

            resolution = app->opt.fb_size_resolver(width, (int) target_height);
        }

        app->indirect_target.resize(resolution.x, resolution.y);
    }

    static void run_logic(app_t &parent) {
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
                parent.world.enqueue_render();
                parent.frame_no++;
            }
        }

        CEL_TRACE("Exit run logic");
    }

    app_t::app_t(int argc, char **argv) : opt(argc, argv), win(this), fullscreen_quad(1, qvbo), menus(this), world(this), logic_thread(run_logic, std::ref(*this)) {
        glfwSetWindowUserPointer(win.get_handle(), this);
        glfwSetFramebufferSizeCallback(win.get_handle(), framebuffer_size_callback);

        // This is a very confusing and counter-intuative place to initialize the projection matrix but eh
        ortho_proj = gl_mat4::ortho(-1, 1, -1, 1, cel::znear, cel::zfar);
        framebuffer_size_callback(win.get_handle(), opt.win_width, opt.win_height); // initialize ortho_proj and indirect_target accordingly

        shader vert{GL_VERTEX_SHADER, read_entire_file(opt.res_path("vert/default.vert"))};
        shader frag{GL_FRAGMENT_SHADER, read_entire_file(opt.res_path("frag/default.frag"))};
        default_shaders.attach(vert.get_id());
        default_shaders.attach(frag.get_id());
        default_shaders.link();

        default_shaders.use();
        su_proj = default_shaders.get_uniform("projection_mat");
        glUniform1i(default_shaders.get_uniform("tex"), 0);
        gl_mat4 ident{identity_tag};
        glUniformMatrix4fv(default_shaders.get_uniform("view_mat"), 1, GL_FALSE, (const GLfloat *) &ident);

        fullscreen_quad.instances[0] = draw_instance{gl_mat2{identity_tag}, {0, 0, -0.5}, 1.0f, {0, 0}, {1, 1}, {1, 1}};
        fullscreen_quad.num_blits = 1;
        fullscreen_quad.upload();
    }

    app_t::~app_t() {
        CEL_TRACE("~app()");

        logic_running = false;
        logic_frames_due = 10;
        logic_convar.notify_all();

        if (logic_thread.joinable()) logic_thread.join();
        else logic_thread.detach();
    }

    void app_t::run() {
        float target_fps = 60.0f;
        float logic_target_fps = 60.0f;
        bool paused = false;
        logic_frames_due = std::numeric_limits<uint64_t>::max();
        logic_convar.notify_one();

        world.layers.emplace_back(world.get_layer<dbg::tgrs_layer>());
        layer *selected = nullptr;
        
        while (win.running()) {
            render_timer.tick();

            menus.new_frame();

            indirect_target.bind();
            // CEL_TRACE("targ = {}, {}", indirect_target.width, indirect_target.height);
            glViewport(0, 0, indirect_target.width, indirect_target.height);
            glClearColor(0.5, 0.5, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            {
                std::lock_guard lg(frame_mtx);
                world.upload();
            }

            world.draw();

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            // CEL_TRACE("win = {}, {}", opt.win_width, opt.win_height);
            glViewport(0, 0, opt.win_width, opt.win_height); // these properties get updated by a callback
            glClearColor(0, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            glActiveTexture(GL_TEXTURE0);
            indirect_target.bind_texture();
            default_shaders.use();
            glUniformMatrix4fv(su_proj, 1, GL_FALSE, (const GLfloat *) &ortho_proj);
            fullscreen_quad.dispatch();

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

            if (ImGui::BeginListBox("Layers")) {
                for (std::size_t i = 0; i < world.layers.size(); i++) {
                    const auto &l = world.layers.at(i);

                    if (ImGui::Selectable(fmt::format("{}##{}", typeid(*l.get()).name(), i).c_str(), selected == l.get())) {
                        selected = l.get();
                    }
                }
                ImGui::EndListBox();
            }

            ImGui::Text("Selected: %lu", reinterpret_cast<uint64_t>(selected));

            if (ImGui::Button("Pop Layer")) world.layers.pop_back();
            ImGui::SameLine();
            if (ImGui::Button("Push Layer")) { 
                world.clear_layer_cache(); // prevent get_layer() from returning a pointer to the same layer
                world.layers.emplace_back(world.get_layer<dbg::tgrs_layer>());
            }

            ImGui::End();

            menus.draw();

            glfwSwapBuffers(win.get_handle());
            glfwPollEvents();
            flush_gl_errors();
        }
    }
}