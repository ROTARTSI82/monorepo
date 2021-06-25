#include "main_helper.h"
#include "linalg.h"
#include <stdio.h>
#include <time.h>

#include <zlib.h>

int main() {
    app_t app;
    start(&app);

    app.view_mat = *(const smat4 *) sm4_identity; // mumble mumble aliasing

    app.renderer.do_flush = 1;
    app.renderer.num_blits = 2;

    vec2 tmp_scale;
    tmp_scale.x = 15;
    tmp_scale.y = 5;

    // terrain foreground
    app.renderer.local_array[1].alpha_mult = 1;
    app.renderer.local_array[1].num_tiles.x = 3;
    app.renderer.local_array[1].num_tiles.y = 1;
    app.renderer.local_array[1].sampling_bottom_left.x = 0;
    app.renderer.local_array[1].sampling_bottom_left.y = 0;
    app.renderer.local_array[1].sampling_extent.x = 1;
    app.renderer.local_array[1].sampling_extent.y = 0.5;
    app.renderer.local_array[1].trans.form = sm2_transform(&tmp_scale, PI);

    app.renderer.local_array[1].trans.late.v3.x = 0;
    app.renderer.local_array[1].trans.late.v3.y = 0;
    app.renderer.local_array[1].trans.late.v3.z = -5;

    tmp_scale = v2_mults(&tmp_scale, 2);
    // starry night background
    app.renderer.local_array[0].alpha_mult = 1;
    app.renderer.local_array[0].num_tiles.x = 3;
    app.renderer.local_array[0].num_tiles.y = 1;
    app.renderer.local_array[0].sampling_bottom_left.x = 0;
    app.renderer.local_array[0].sampling_bottom_left.y = 0.5;
    app.renderer.local_array[0].sampling_extent.x = 1;
    app.renderer.local_array[0].sampling_extent.y = 0.5;
    app.renderer.local_array[0].trans.form = sm2_transform(&tmp_scale, 0);

    app.renderer.local_array[0].trans.late.v3.x = 0;
    app.renderer.local_array[0].trans.late.v3.y = 0;
    app.renderer.local_array[0].trans.late.v3.z = -10;
    

    glUseProgram(app.default_shader);
    glActiveTexture(GL_TEXTURE0);
    glUniform1i(app.su_tex_samp, 0);

    uint32_t frame_num = 0; // tmp

    vec3 off;
    off.x = -0.1;
    off.y = 0;
    off.z = 0;
    smat4 translate_right = sm4_translate(&off);
    off.x = -off.x;
    smat4 translate_left = sm4_translate(&off);
    off.x = 0;
    off.y = -0.1;
    smat4 translate_up = sm4_translate(&off);
    off.y = -off.y;
    smat4 translate_down = sm4_translate(&off);

    smat4 rot = sm4_rotate(PI / 180);
    smat4 rot2 = sm4_rotate(-PI / 180);

    while (!glfwWindowShouldClose(app.win)) {
        tick_fps_limiter(&app.limiter);

        // tmp
        // if (frame_num++ > 0) {
        //     CEL_FB_WIDTH *= 0.9f;
        //     CEL_FB_HEIGHT *= 0.9f;
        //     resize_fbo(&app.renderer, (GLsizei) CEL_FB_WIDTH, (GLsizei) CEL_FB_HEIGHT);

        //     // isn't needed since the aspect ratio remains the same but good practice.
        //     app.perspective = perspective(CEL_FOV, CEL_FB_WIDTH / CEL_FB_HEIGHT, CEL_ZNEAR, CEL_ZFAR);
        // }

        if (glfwGetKey(app.win, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            sm4_mult_eq(&app.view_mat, &translate_right);
        }
        if (glfwGetKey(app.win, GLFW_KEY_LEFT) == GLFW_PRESS) {
            sm4_mult_eq(&app.view_mat, &translate_left);
        }
        if (glfwGetKey(app.win, GLFW_KEY_DOWN) == GLFW_PRESS) {
            sm4_mult_eq(&app.view_mat, &translate_down);
        }
        if (glfwGetKey(app.win, GLFW_KEY_UP) == GLFW_PRESS) {
            sm4_mult_eq(&app.view_mat, &translate_up);
        }

        if (glfwGetKey(app.win, GLFW_KEY_A) == GLFW_PRESS) {
            sm4_mult_eq(&app.view_mat, &rot2);
        }
        if (glfwGetKey(app.win, GLFW_KEY_S) == GLFW_PRESS) {
            sm4_mult_eq(&app.view_mat, &rot);
        }

        flush_gl_errors();

        glViewport(0, 0, (GLsizei) CEL_FB_WIDTH, (GLsizei) CEL_FB_HEIGHT);
        
        glBindTexture(GL_TEXTURE_2D, app.TMP_TEST_TEX);
        glUniformMatrix4fv(app.su_proj_mat, 1, GL_FALSE, (const GLfloat *) &app.perspective);
        glUniformMatrix4fv(app.su_view_mat, 1, GL_FALSE, (const GLfloat *) &app.view_mat);
        glClearColor(1, 0, 0, 1);
        render_locked(&app.renderer, &app.rend_mtx);

        glViewport(0, 0, app.win_width, app.win_height);

        glBindTexture(GL_TEXTURE_2D, app.renderer.fbo_tex);
        glUniformMatrix4fv(app.su_proj_mat, 1, GL_FALSE, (const GLfloat *) &app.orthographic);
        glUniformMatrix4fv(app.su_view_mat, 1, GL_FALSE, (const GLfloat *) &sm4_identity);
        glClearColor(0, 1, 0, 1);
        render(&app.master_ctx);

        glfwSwapBuffers(app.win);
        glfwPollEvents();
    }

    stop(&app);
    return 0;
}
