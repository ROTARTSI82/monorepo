//
// Created by 25granty on 10/28/19.
//

#pragma once

#ifndef __MASON_FT_CORE_H
#define __MASON_FT_CORE_H

#include "ft2build.h"
#include FT_FREETYPE_H

#include "mason/log.h"
#include "mason/gl/vertex_buffer.h"
#include "mason/gl/shader_program.h"

#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"

namespace mason::ft {

    extern FT_Library *ft_lib;
    extern mason::gl::shader_program *prog;

    void init_ft();

    void quit_ft();
}

#endif //__MASON_FT_CORE_H
