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
    smat3 trans = sm3_translate(&totrans);

    smat3 comb = sm3_mult(&scale, &rot);
    printsm3(&comb);

    sm3_mult_eq(&comb, &trans);
    printsm3(&comb);

    smat3 shortcut = sm3_transform(&totrans, &toscale, PI);
    printsm3(&shortcut);

    while (!glfwWindowShouldClose(app.win)) {
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        glfwSwapBuffers(app.win);
        glfwPollEvents();
    }

    stop(&app);
    return 0;
}
