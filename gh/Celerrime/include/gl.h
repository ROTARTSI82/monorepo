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
} draw_instance_t;

typedef struct render_ctx_t {
    GLuint framebuffer;
    GLuint fbo_tex;
    // we aren't using depth or stencil so no render buffer object

    GLuint instance_vbo;
    unsigned num_blits;
    unsigned max_blits;
    GLsizeiptr vbo_size;

    GLuint vao;
} render_ctx_t;

void init_fbo(render_ctx_t *ctx, GLsizei width, GLsizei height);

void compile_and_check_shader(GLuint shader);

void render(render_ctx_t *ctx);
void fill_vao_and_vbo(GLuint global_vbo, render_ctx_t *ctx, unsigned max_blits);

void new_tex_from_file(const char *filename, GLuint *tex);

GLenum proc_gl_error();

void flush_gl_errors();

#endif //GAMETEST_GL_H
