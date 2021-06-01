#include "main_helper.h"
#include "linalg.h"
#include <stdio.h>
#include <time.h>

int main() {
//    uint64_t seed = 0x5c27d496d1868dc8;

//    uint8_t seed = 0xd4;

    for (int k = 0; k < 256; k++) {
        uint64_t orig = rand();
        uint64_t seed = orig;

        uint64_t bitcounts[64];
        memset(bitcounts, 0, 512);

        uint8_t run = 1;
        uint64_t it = 0;
        while (run) {
            it++;
            seed = rand_nextl(seed);
            run = (seed != orig) && (it < 65536);

            uint64_t cpy = seed;
            for (int j = 0; j < 64; j++) {
                bitcounts[j] += cpy & 1;
                cpy >>= 1;
            }
        }

        printf("it = %lu, orig = %u\n", it, orig);

        for (int i = 0; i < 64; i++) {
            printf("bit%i = %lu\n", i, bitcounts[i]);
        }

        printf("\n\n==============================");
    }

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
