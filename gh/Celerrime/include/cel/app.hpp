/**
 * @file app.hpp
 * @brief 
 * @date 2021-07-11
 */

#pragma once

#ifndef CEL_APP_HPP
#define CEL_APP_HPP

#include "cel/eng/window.hpp"
#include "cel/settings.hpp"
#include "cel/game/imgui_menus.hpp"
#include "cel/eng/misc_util.hpp"
#include "cel/world.hpp"

#include <thread>
#include <mutex>
#include <condition_variable>
#include <atomic>

namespace cel {
    void init();
    void quit();

    class app_t {
    public:
        settings_handler opt;
        gl_mat4 ortho_proj;
        window win; // the window MUST go first for the ogl context to be created.
        framebuffer indirect_target{2, 2}; // placeholder. Is assigned in call to framebuffer_size_callback
        shader_pipeline default_shaders{};
        GLint su_proj;

        texture_manager tex_pool{&opt};

        quad_vbo qvbo{};
        draw_call fullscreen_quad;
        imgui_menu menus;
        fps_limiter render_timer{};

        fps_limiter logic_timer{};
        world_t world;
        std::condition_variable logic_convar;
        std::mutex frame_mtx, frames_due_mtx;
        uint64_t frame_no = 0, logic_frames_due = 0;
        std::atomic_bool logic_running = true;
        std::thread logic_thread; // after all the data for logic_thread is initialized!


        app_t(int argc, char **argv);
        ~app_t();

        inline window &get_window() { return win; }

        // finer control in the future?
        void run();
    };
}

#endif
