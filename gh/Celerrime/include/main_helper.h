//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef GAMETEST_MAIN_HELPER_C
#define GAMETEST_MAIN_HELPER_C

#include "common.h"
#include "config.h"
#include <memory.h>

static void on_win_resize(GLFWwindow* window, int width, int height) {
    glViewport(0, 0, width, height);

#if GUPDATE_PROJECTION
    app_t *app = glfwGetWindowUserPointer(window);
    app->projection = perspective(GFOV, (float) width / (float) height, ZNEAR, ZFAR);
#endif

}

// TODO: Why do I have to mark these functions extern to get them to link?
extern inline void start(app_t *app) {
    app->master_ctx.framebuffer = 0; // default framebuffer to actually draw on the screen
    app->projection = perspective(GFOV, (float) GWIN_WIDTH / (float) GWIN_HEIGHT, ZNEAR, ZFAR);
    app->view_mat = *((smat4 *) sm4_identity); // should we just use a memcpy?
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
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDisable(GL_STENCIL_TEST);
    glViewport(0, 0, GWIN_WIDTH, GWIN_HEIGHT);

    // counter-clockwise definition. culling is useless in 2d tho
    float quad[] = {
        -1, -1,
        1, -1,
        1, 1,
        -1, 1
    };

    glGenBuffers(1, &app->quad_vbo);

    glBindBuffer(GL_ARRAY_BUFFER, app->quad_vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(quad), quad, GL_STATIC_DRAW);

    fill_vao_and_vbo(app->quad_vbo, &app->master_ctx, MAX_BLITS);


    {
        app->master_ctx.num_blits = 2;
        glBindBuffer(GL_ARRAY_BUFFER, app->master_ctx.instance_vbo);
        draw_instance_t *buf = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY);

        vec2 scale1;

        scale1.x = 30.414331436157227f;
        scale1.y = 2.339162826538086f;

        vec2 scale2;
        scale2.x = 104.417f;
        scale2.y = 8.03068f;

        vec3 trans1, offset;
        trans1.x = 0;
        trans1.y = 2.3963f;
        trans1.z = -42.2149f;

        offset.x = 0;
        offset.y = 0;
        offset.z = 0;

        buf[1].transform = sm4_transform(&offset, &scale1, 0);


        buf[1].sampling_bottom_left = *(vec2*)&offset;
        scale1.y = 1;
        buf[1].sampling_extent = scale1;

        buf[0].transform = sm4_transform(&trans1, &scale2, PI);


        buf[0].sampling_bottom_left = *(vec2*)&offset;
        scale2.y = 1;
        buf[0].sampling_extent = scale2;

        glFlush();
        EXIF(!glUnmapBuffer(GL_ARRAY_BUFFER), "buffer datastore corrupt")
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    long frag_size, vert_size;
    GLchar *frag = full_read_file("./res/shader/default.frag", &frag_size);
    frag[frag_size] = '\0';

    printf("Fragment Shader:\n%s\n", frag);

    GLchar *vert = full_read_file("./res/shader/default.vert", &vert_size);
    vert[vert_size] = '\0';

    printf("Vertex Shader:\n%s\n", vert);

    GLuint vert_sp = glCreateShader(GL_VERTEX_SHADER);
    GLuint frag_sp = glCreateShader(GL_FRAGMENT_SHADER);

    GLint frag_sizei = (GLint) frag_size, vert_sizei = (GLint) vert_size;
    glShaderSource(vert_sp, 1, (const GLchar *const *) &vert, &vert_sizei);
    glShaderSource(frag_sp, 1, (const GLchar *const *) &frag, &frag_sizei);

    compile_and_check_shader(vert_sp);
    compile_and_check_shader(frag_sp);

    app->default_shader = glCreateProgram();
    glAttachShader(app->default_shader, vert_sp);
    glAttachShader(app->default_shader, frag_sp);
    glLinkProgram(app->default_shader);
    glUseProgram(app->default_shader);

    glDeleteShader(vert_sp);
    glDeleteShader(frag_sp);

    free((void *) frag);
    free((void *) vert);

    app->su_proj_mat = glGetUniformLocation(app->default_shader, "projection_mat");
    app->su_view_mat = glGetUniformLocation(app->default_shader, "view_mat");
    app->su_tex_samp = glGetUniformLocation(app->default_shader, "tex");

    {
        glGenTextures(1, &app->TMP_TEST_TEX);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, app->TMP_TEST_TEX);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        unsigned char *texdat = malloc(40*40*4);
        memset(texdat, 0x00, 20*40*4);
        unsigned char v = 0;
        for (unsigned char *i = texdat + 20*40*4; i < texdat + 40*40*4; i++) {
            *i = rand_table[v++];
        }
//        memset(texdat, 0x88, 40*40*4);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 40, 40, 0, GL_RGBA, GL_UNSIGNED_BYTE, texdat);
        free(texdat);

        glGenerateMipmap(GL_TEXTURE_2D);
    }
}

extern inline void stop(app_t *app) {
    glDeleteBuffers(1, &app->quad_vbo);
    glDeleteVertexArrays(1, &app->master_ctx.vao);
    glDeleteBuffers(1, &app->master_ctx.instance_vbo);
    glDeleteProgram(app->default_shader);

    glfwDestroyWindow(app->win);
    glfwTerminate();
}


#endif //GAMETEST_MAIN_HELPER_C
