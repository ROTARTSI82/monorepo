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
    app.renderer.num_blits = 1;

    vec2 tmp_scale;
    tmp_scale.x = 5;
    tmp_scale.y = 5;

    // starry night background
    app.renderer.local_array[0].alpha_mult = 1;
    app.renderer.local_array[0].num_tiles.x = 1;
    app.renderer.local_array[0].num_tiles.y = 1;
    app.renderer.local_array[0].sampling_bottom_left.x = 0;
    app.renderer.local_array[0].sampling_bottom_left.y = 0;
    app.renderer.local_array[0].sampling_extent.x = 1;
    app.renderer.local_array[0].sampling_extent.y = 0.5;
    app.renderer.local_array[0].transform = sm2_transform(&tmp_scale, PI);

    app.renderer.local_array[0].translate.x = 0;
    app.renderer.local_array[0].translate.y = 0;
    app.renderer.local_array[0].translate.z = -5;

    glUseProgram(app.default_shader);
    glActiveTexture(GL_TEXTURE0);
    glUniform1i(app.su_tex_samp, 0);

    uint32_t frame_num = 0; // tmp

    while (!glfwWindowShouldClose(app.win)) {
        tick_fps_limiter(&app.limiter);

        // tmp
        if (frame_num++ > 240) {
            GFB_WIDTH *= 0.9f;
            GFB_HEIGHT *= 0.9f;
            resize_fbo(&app.renderer, (GLsizei) GFB_WIDTH, (GLsizei) GFB_HEIGHT);

            // isn't needed since the aspect ratio remains the same but good practice.
            app.perspective = perspective(GFOV, GFB_WIDTH / GFB_HEIGHT, ZNEAR, ZFAR);
        }

        flush_gl_errors();

        glViewport(0, 0, (GLsizei) GFB_WIDTH, (GLsizei) GFB_HEIGHT);
        
        glBindTexture(GL_TEXTURE_2D, app.TMP_TEST_TEX);
        glUniformMatrix4fv(app.su_proj_mat, 1, GL_FALSE, (const GLfloat *) &app.perspective);
        glUniformMatrix4fv(app.su_view_mat, 1, GL_FALSE, (const GLfloat *) &app.view_mat);
        glClearColor(1, 0, 0, 1);
        render(&app.renderer);

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
