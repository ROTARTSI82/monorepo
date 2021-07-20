/**
 * @file render.hpp
 * @brief 
 * @date 2021-07-11
 */

#pragma once

#ifndef CEL_ENG_RENDER_HPP
#define CEL_ENG_RENDER_HPP

#include <string>
#include <vector>

#include "cel/eng/linalg.hpp"
#include "cel/eng/pmr_list.hpp"
#include "cel/settings.hpp"

namespace cel {

    struct draw_instance {

    };

    class draw_call {
    public:
        std::vector<draw_instance> instances;

        virtual void dispatch() = 0;
    };
    
    class gui_widget {
    public:
        gui_widget *anchor = nullptr; // nullptr for window.
        vec2 coords{}; // relative to anchor.
        bool rebuild_required = false;


    };

    /**
     * @brief Abstract interface for rendering to support multiple backends.
     *  
     * Each thread MUST only have a single `renderer` object, and it MUST
     * be created on that thread. (Necessary for OpenGL contexts.)
     */
    class renderer {
    protected:

    public:

        std::vector<draw_call> world_draws;
        std::vector<draw_call> gui_draws;

        settings_handler *settings;

        bool win_enforce_aspect; // window-specific toggle for enforcing aspect ratios.

        renderer(int width, int height, const std::string &name);
        virtual ~renderer() = 0;

        virtual void next_frame() = 0;
    };
}

#endif
