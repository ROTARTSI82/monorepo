#include "main_helper.h"
#include "linalg.h"
#include <stdio.h>

int main() {
    app_t app;
    start(&app);

    printsm4((smat4 *) sm4_identity);
    smat4 rot = sm4_rotate(PI);

    vec2 toscale;
    toscale.x = 2;
    toscale.y = 3;
    smat4 scale = sm4_scale(&toscale);

    vec3 totrans;
    totrans.x = 4;
    totrans.y = 5;
    totrans.z = 0;
    smat4 trans = sm4_translate(&totrans);

    smat4 comb = sm4_mult(&scale, &rot);
    printsm4(&comb);

    sm4_mult_eq(&comb, &trans);
    printsm4(&comb);

    smat4 shortcut = sm4_transform(&totrans, &toscale, PI);
    printsm4(&shortcut);

    float theta = 0.0f;
    float scroll = 0;

    while (!glfwWindowShouldClose(app.win)) {
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        {
//            glBindBuffer(GL_ARRAY_BUFFER, app.master_ctx.instance_vbo);
//            draw_instance_t *buf = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY);
//
//            vec2 s;
//            s.x = 40;
//            s.y = 8*tanf(GFOV * PI / 180);
//
//
//            vec2 s2;
//            s2.x = 180;
//            s2.y = 24*tanf(GFOV * PI / 180);
//
            vec3 o;
            o.x = 0;
            o.y = 0;
            o.z = -11.1958f;
//
//            buf->transform = sm4_transform(&o, -2, &s, 0);
//            buf[1].transform = sm4_transform(&o, -6, &s, PI);
//
//            glFlush();
//            EXIF(!glUnmapBuffer(GL_ARRAY_BUFFER), "buffer datastore corrupt")
//            glBindBuffer(GL_ARRAY_BUFFER, 0);

            scroll += 0.01f;
            o.x = (-5) + scroll;
            if (scroll > 10) { scroll = 0;}
            app.view_mat = sm4_translate(&o);
        }

        glUseProgram(app.default_shader);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, app.TMP_TEST_TEX);
        glUniform1i(app.su_tex_samp, 0);
        glUniformMatrix4fv(app.su_proj_mat, 1, GL_FALSE, (const GLfloat *) &app.projection);
        glUniformMatrix4fv(app.su_view_mat, 1, GL_FALSE, (const GLfloat *) &app.view_mat);
        glBindVertexArray(app.master_ctx.vao);

        render(&app.master_ctx);

        glfwSwapBuffers(app.win);
        glfwPollEvents();
    }

    stop(&app);
    return 0;
}
