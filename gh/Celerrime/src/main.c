#include "main_helper.h"
#include "linalg.h"
#include <stdio.h>

int main() {
    app_t app;
    start(&app);

    printsm3((smat3 *) sm3_identity);
    smat3 rot = sm3_rotate(PI);

    vec2 toscale;
    toscale.x = 2;
    toscale.y = 3;
    smat3 scale = sm3_scale(&toscale);

    vec2 totrans;
    totrans.x = 4;
    totrans.y = 5;
    smat3 trans = sm3_translate(&totrans, 1);

    smat3 comb = sm3_mult(&scale, &rot);
    printsm3(&comb);

    sm3_mult_eq(&comb, &trans);
    printsm3(&comb);

    smat3 shortcut = sm3_transform(&totrans, 1, &toscale, PI);
    printsm3(&shortcut);

    float theta = 0.0f;

    while (!glfwWindowShouldClose(app.win)) {
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        {
            glBindBuffer(GL_ARRAY_BUFFER, app.master_ctx.instance_vbo);
            draw_instance_t *buf = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY);

            vec2 s;
            s.x = 0.5;
            s.y = 0.5;

            buf->transform = sm3_rotate(theta);
            smat3 trans2 = sm3_scale(&s);
            sm3_mult_eq(&buf->transform, &trans2);
            theta += PI / 360;

            glFlush();
            EXIF(!glUnmapBuffer(GL_ARRAY_BUFFER), "buffer datastore corrupt")
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

        glUseProgram(app.default_shader);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, app.TMP_TEST_TEX);
        glUniform1i(app.su_tex_samp, 0);
        glUniformMatrix4fv(app.su_proj_mat, 1, GL_FALSE, (const GLfloat *) &app.projection);
        glUniformMatrix3fv(app.su_view_mat, 1, GL_FALSE, (const GLfloat *) &app.view_mat);
        glBindVertexArray(app.master_ctx.vao);

        render(&app.master_ctx);

        glfwSwapBuffers(app.win);
        glfwPollEvents();
    }

    stop(&app);
    return 0;
}
