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

void *full_read_file(const char *filename, long *size);
//void overwrite_file(const char *filename, void *data, size_t len) { // len being length in bytes
//    FILE *fp = fopen(filename, "w");
//
//}

typedef struct {
    GLFWwindow *win;
    GLuint quad_vbo;

    GLuint default_shader;
    GLint su_view_mat;
    GLint su_proj_mat;
    GLint su_tex_samp;

    smat4 projection;
    smat4 view_mat;

    GLuint TMP_TEST_TEX;

    render_ctx_t master_ctx;
} app_t;


#endif //GAMETEST_COMMON_H
