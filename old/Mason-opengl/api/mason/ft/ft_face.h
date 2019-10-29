//
// Created by 25granty on 10/28/19.
//

#pragma once

#ifndef __MASON_FT_FACE_H
#define __MASON_FT_FACE_H

#include "mason/ft/ft_core.h"
#include "mason/ft/ft_glyph.h"
#include "mason/gl/vertex_buffer.h"

#include <unordered_map>

// TODO: Make the specialized shader!!!

namespace mason::ft {
    class ft_face {
    public:
        GLuint vbo;
        glm::mat4 proj;
        FT_Face internal_face;
        std::unordered_map<char, mason::ft::ft_glyph> glyphs;

        ft_face(const std::string &font, FT_UInt size);

        ~ft_face();

        void set_screen_size(glm::vec2 size);

        /*
         * `msg` is the string you want to render
         * `pos` is the position you want to render it at, in {x, y}
         * `scale` is the scale of the text
         * `col` is the color you want to render it in, in {r, g, b, a}
         */
        void render(const std::string &msg, glm::vec2 pos, float scale, glm::vec4 col);

        ft_glyph get_glyph(char x);
    };
}

#endif //__MASON_FT_FACE_H
