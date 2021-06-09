//
// Created by grant on 5/29/21.
//

#pragma once

#ifndef GAMETEST_GL_H
#define GAMETEST_GL_H

#include "linalg.h"

#include <GL/glew.h>
#include <GLFW/glfw3.h>

typedef struct draw_instance_t {
    smat2 transform;
    vec3 translate;
    float alpha_mult;

    vec2 sampling_bottom_left;
    vec2 sampling_extent;
    // number of times we want to tile the texture in each axis.
    vec2 num_tiles;
} draw_instance_t;

typedef struct render_ctx_t {
    GLuint framebuffer;
    GLuint fbo_tex;
    // we aren't using depth or stencil so no render buffer object

    GLuint instance_vbo;
    unsigned num_blits;
    unsigned max_blits;

    GLuint vao;

    draw_instance_t *local_array;
    uint8_t do_flush;
} render_ctx_t;

// MUST be called AFTER init_ctx()
void init_fbo(render_ctx_t *ctx, GLsizei width, GLsizei height);
void resize_fbo(render_ctx_t *ctx, GLsizei width, GLsizei height);

void compile_and_check_shader(GLuint shader);

void render(render_ctx_t *ctx);
void init_ctx(GLuint global_vbo, render_ctx_t *ctx, unsigned max_blits);
void destroy_ctx(render_ctx_t *ctx);

void new_tex_from_file(const char *filename, GLuint *tex);

GLenum proc_gl_error();

void flush_gl_errors();

#endif //GAMETEST_GL_H
