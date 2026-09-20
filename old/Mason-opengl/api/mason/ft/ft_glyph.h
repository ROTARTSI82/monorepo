//
// Created by 25granty on 10/28/19.
//

#pragma once

#ifndef __MASON_FT_CHARACTER_H
#define __MASON_FT_CHARACTER_H

#include "mason/gl/gl_core.h"

#include "glm/glm.hpp"

#include "ft2build.h"
#include FT_FREETYPE_H

namespace mason::ft {
    struct ft_glyph {
        GLuint tex_id;             // ID handle of the glyph texture
        glm::ivec2 size;               // Size of glyph
        glm::ivec2 bearing;            // Offset from baseline to left/top of glyph
        FT_Pos advance;            // Offset to advance to next glyph
    };
}

#endif //__MASON_FT_CHARACTER_H
