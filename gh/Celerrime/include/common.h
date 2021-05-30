//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef GAMETEST_COMMON_H
#define GAMETEST_COMMON_H

#include <stdio.h>
#include <stdlib.h>


#include "gl.h"

// EXit IF
#define EXIF(exp, msg) if (exp) { printf(msg); exit(1); }


typedef struct {
    GLFWwindow *win;
    GLuint quad_vbo;

    render_ctx_t master_ctx;
} app_t;


#endif //GAMETEST_COMMON_H
