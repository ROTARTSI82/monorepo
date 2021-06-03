#include "main_helper.h"
#include "linalg.h"
#include <stdio.h>
#include <time.h>

int main() {
//    uint64_t seed = 0x5c27d496d1868dc8;

//    uint8_t seed = 0xd4;

    long size;
    void *cont = full_read_file("./res/test.rcmp", &size);
    size_t s;
    uint8_t *out = decompress(cont, &s);

    printf("out = %s\n", out);
//    out[s] = '\0';
//    printf("OUT = %s\n", out);

    free(out);
    free(cont);

    app_t app;
    start(&app);

    float scroll = 0;

    while (!glfwWindowShouldClose(app.win)) {
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        {
            vec3 o;
            o.x = 0;
            o.y = 0;
            o.z = -11.1958f;

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
