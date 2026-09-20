//
// Created by 25granty on 10/28/19.
//

#include "mason/ft/ft_core.h"

namespace mason::ft {

    FT_Library *ft_lib = nullptr;
    mason::gl::shader_program *prog = nullptr;

    void init_ft() {
        if (ft_lib != nullptr || prog != nullptr) {
            MASON_WARN("init_ft() called when freetype already initialized! Skipping!");
            return;
        }

        prog = new mason::gl::shader_program({
                                                     {"fragment-shader", "#version 120\n"
                                                                         "varying vec2 v_uv;\n"
                                                                         "uniform sampler2D tex;\n"
                                                                         "uniform vec4 color;\n"
                                                                         "void main() {\n"
                                                                         "    vec4 sampled = vec4(1.0, 1.0, 1.0, texture2D(tex, v_uv).r);\n"
                                                                         "    gl_FragColor = color * sampled;\n"
                                                                         "}\n"},
                                                     {"vertex-shader",   "#version 120\n"
                                                                         "attribute vec4 vertex;\n"
                                                                         "varying vec2 v_uv;\n"
                                                                         "uniform mat4 projection;\n"
                                                                         "void main() {\n"
                                                                         "    gl_Position = projection * vec4(vertex.xy, 0.0, 1.0);\n"
                                                                         "    v_uv = vertex.zw;\n"
                                                                         "}\n"}});

        ft_lib = new FT_Library();
        if (FT_Init_FreeType(ft_lib)) {
            MASON_WARN("Failed to initialize freetype!");
        }

        FT_Int major, minor, patch;
        FT_Library_Version(*ft_lib, &major, &minor, &patch);
        MASON_INFO("Successfully initialized freetype {}.{}.{}", major, minor, patch);
    }

    void quit_ft() {
        if (ft_lib == nullptr || prog == nullptr) {
            MASON_WARN("quit_ft() called before init_ft()! Skipping!");
            return;
        }

        delete prog;

        FT_Done_FreeType(*ft_lib);
        delete ft_lib;
        ft_lib = nullptr;
    }
}
