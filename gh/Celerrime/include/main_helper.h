//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef GAMETEST_MAIN_HELPER_CPP
#define GAMETEST_MAIN_HELPER_CPP

#include "common.h"
#include "config.h"
#include <memory.h>

static void on_win_resize(GLFWwindow* window, int width, int height) {
    glViewport(0, 0, width, height);

    app_t *app = glfwGetWindowUserPointer(window);
    app->master_ctx.projection = perspective(GFOV, (float) width / (float) height, ZNEAR, ZFAR);
}

// TODO: Why do I have to mark these functions extern to get them to link?
extern inline void start(app_t *app) {
    app->master_ctx.framebuffer = 0; // default framebuffer to actually draw on the screen
    app->master_ctx.projection = perspective(GFOV, (float) GWIN_WIDTH / (float) GWIN_HEIGHT, ZNEAR, ZFAR);
    app->master_ctx.view_offset.x = 0;
    app->master_ctx.view_offset.y = 0;

    glfwInit();

    app->win = glfwCreateWindow(GWIN_WIDTH, GWIN_HEIGHT, "Game Test", NULL, NULL);
    EXIF(app->win == NULL,  "GLFW window creation failed")
    glfwMakeContextCurrent(app->win);
    glfwSwapInterval(1);
    glfwSetWindowUserPointer(app->win, app);

    glfwSetFramebufferSizeCallback(app->win, on_win_resize);

    EXIF(glewInit() != GLEW_OK, "GLEW initialization failed")

    printf("OpenGL initialized: Renderer %s version %s\n", glGetString(GL_RENDERER), glGetString(GL_VERSION));

    glDisable(GL_DEPTH_TEST); // More efficient if we just render it in order lol
    glDisable(GL_CULL_FACE);
    glViewport(0, 0, GWIN_WIDTH, GWIN_HEIGHT);

    // counter-clockwise definition. culling is useless in 2d tho
    float quad[] = {
        0, 0,
        1, 0,
        1, 1,
        0, 1
    };

    glGenBuffers(1, &app->quad_vbo);

    glBindBuffer(GL_ARRAY_BUFFER, app->quad_vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(quad), quad, GL_STATIC_DRAW);

    fill_vao_and_vbo(app->quad_vbo, &app->master_ctx, MAX_BLITS);

    glBindBuffer(GL_ARRAY_BUFFER, app->master_ctx.instance_vbo);
    void *buf = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY);
    memset(buf, 0, app->master_ctx.vbo_size);
    glFlush();
    EXIF(!glUnmapBuffer(GL_ARRAY_BUFFER), "buffer datastore corrupt")
    glBindBuffer(GL_ARRAY_BUFFER, 0);

//    glGenVertexArrays(1, &app->default_vao);
//    // take advantage of already-bound buffer above

}

extern inline void stop(app_t *app) {
    glDeleteBuffers(1, &app->quad_vbo);
    glDeleteVertexArrays(1, &app->master_ctx.vao);
    glDeleteBuffers(1, &app->master_ctx.instance_vbo);

    glfwDestroyWindow(app->win);
    glfwTerminate();
}


#endif //GAMETEST_MAIN_HELPER_CPP
