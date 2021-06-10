//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef GAMETEST_COMMON_H
#define GAMETEST_COMMON_H

#include <stdio.h>
#include <stdlib.h>


#include "gl.h"
#include "file_reader.h"
#include "util.h"

#include <time.h>
#include <pthread.h>

// EXit IF
#define EXIF(exp, msg) if (exp) { printf(msg); exit(1); }

// for easy disabling of print in release
#define PRINT(...) printf(__VA_ARGS__)

typedef struct settings_t {
    uint32_t win_height;
    uint32_t win_width;

    int fps_cap; // -1 = no limit, 0 = vsync
} settings_t;

typedef struct logic_thread_data_t {
    fps_limiter_t limiter;
} logic_thread_data_t;

typedef struct app_t {
    GLFWwindow *win;
    int win_width, win_height;
    fps_limiter_t limiter;

    pthread_t logic_thread;
    volatile uint8_t is_logic_thread_running;
    logic_thread_data_t lt_dat;

    settings_t settings;

    GLuint quad_vbo;
    GLuint default_shader;
    GLint su_view_mat;
    GLint su_proj_mat;
    GLint su_tex_samp;

    smat4 perspective;
    smat4 orthographic;
    smat4 view_mat;

    GLuint TMP_TEST_TEX;

    render_ctx_t master_ctx; // actually rendering to the window.
    render_ctx_t renderer; // intermediate framebuffer we render to
    pthread_mutex_t rend_mtx;
} app_t;




#endif //GAMETEST_COMMON_H
