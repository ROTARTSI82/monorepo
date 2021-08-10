#include "cel/app.hpp"

#include "cel/eng/log.hpp"
#include "cel/eng/misc_util.hpp"
#include "cel/eng/gl.hpp"

#include <fmt/format.h>

namespace cel {
    static void glfw_error_callback(int code, const char* description) {
        CEL_ERROR("GLFW ERROR {}: {}", code, description);
    }

    void init() {
        glfwSetErrorCallback(glfw_error_callback);
        CEL_EXITIF(!glfwInit(), 1, "GLFW Initialization failed!");

        CEL_INFO("GLFW compiled {}.{}.{}, linked {}. Timer hz = {}", GLFW_VERSION_MAJOR, 
                 GLFW_VERSION_MINOR, GLFW_VERSION_REVISION, glfwGetVersionString(), glfwGetTimerFrequency());
    }

    void quit() {
        CEL_TRACE("glfwTerminate()");
        glfwTerminate();
    }

    app::app(int argc, char **argv) : settings(argc, argv), ctl_panel(&settings, window_types::control) {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glfwSwapInterval(1);

        shader frag{GL_FRAGMENT_SHADER, read_entire_file(settings.path_to("frag/mandelbrot-fast.frag"))};
        shader vert{GL_VERTEX_SHADER, read_entire_file(settings.path_to("vert/mandelbrot.vert"))};

        default_shaders.attach(frag.get_id());
        default_shaders.attach(vert.get_id()); 
        default_shaders.link();

        shader hd_frag{GL_FRAGMENT_SHADER, read_entire_file(settings.path_to("frag/mandelbrot.frag"))};
        hd_shaders.attach(hd_frag.get_id());
        hd_shaders.attach(vert.get_id());
        hd_shaders.link();
    }

    app::~app() {
        CEL_TRACE("~app()");
    }

    void app::run() {
        bool use_hd = false;

        uint64_t frame = 0;
        draw_call test{4096};

        // test.instances[0] = draw_instance{gl_mat2::transform({15, 5}, consts_for<gl_float>::calc().pi), {0, 0, -5}, 1, {0, 0}, {1, 0.5}, {3, 1}};
        test.instances[0] = draw_instance{gl_mat2::transform({3.5, 2}, consts_for<gl_float>::calc().pi), {0, 0, -5}, 1, {-2.5, -1}, {3.5, 2}, {1, 1}};
        test.num_blits = 1;
        test.flush_locked();

        auto proj_su = default_shaders.get_uniform("projection_mat");
        auto view_su = default_shaders.get_uniform("view_mat");
        // auto text_su = default_shaders.get_uniform("tex");

        glActiveTexture(GL_TEXTURE0);
        // texture test_tex{settings.path_to("test.jpg")};

        auto persp = gl_mat4::perspective(90, 1440.0f / 900, 0.1, 100);

        #define M_STRIFY(x) #x
        #define PRINT_MAT(v) fmt::print( M_STRIFY(v) " = [{:.6f}, {:.6f}, {:.6f}, {:.6f}]\n\t[{:.6f}, {:.6f}, {:.6f}, {:.6f}]\n\t[{:.6f}, {:.6f}, {:.6f}, {:.6f}]\n\t[{:.6f}, {:.6f}, {:.6f}, {:.6f}]\n", v.c0.x, v.c1.x, v.c2.x, v.c3.x, \
                    v.c0.y, v.c1.y, v.c2.y, v.c3.y, v.c0.z, v.c1.z, v.c2.z, v.c3.z, v.c0.w, v.c1.w, v.c2.w, v.c3.w);

        #define PRINT_MAT2(v) fmt::print( M_STRIFY(v) " = [{:.6f}, {:.6f}]\n\t[{:.6f}, {:.6f}]\n", v.c0.x, v.c1.x, v.c0.y, v.c1.y);

        gl_mat4 view{identity_tag};

        PRINT_MAT(view);
        PRINT_MAT(persp);

        PRINT_MAT2(test.instances[0].transform);

        while (ctl_panel.running()) {
            int w, h;
            glfwGetFramebufferSize(ctl_panel.get_window(), &w, &h);
            glViewport(0, 0, w, h);
            glClearColor(1, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            // view *= gl_mat4::transform({-0.1, 0, 0}, {1, 1}, 0);

            if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_A) == GLFW_PRESS) {
                view *= gl_mat4::transform({0, 0, 0}, {1.025, 1.025}, 0);
            } else if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_S) == GLFW_PRESS) {
                view *= gl_mat4::transform({0, 0, 0}, {0.975, 0.975}, 0);
            }

            if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_LEFT) == GLFW_PRESS) {
                view *= gl_mat4::transform({0.125, 0, 0}, {1, 1}, 0);
            } else if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_RIGHT) == GLFW_PRESS) {
                view *= gl_mat4::transform({-0.125, 0, 0}, {1, 1}, 0);
            }

            if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_UP) == GLFW_PRESS) {
                view *= gl_mat4::transform({0, -0.125, 0}, {1, 1}, 0);
            } else if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_DOWN) == GLFW_PRESS) {
                view *= gl_mat4::transform({0, 0.125, 0}, {1, 1}, 0);
            }

            if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_F) == GLFW_PRESS) {
                use_hd = false;
            } else if (glfwGetKey(ctl_panel.get_window(), GLFW_KEY_H) == GLFW_PRESS) {
                // use_hd = true;
            }

            // test_tex.bind();
            (use_hd ? hd_shaders : default_shaders).use();
            // glUniform1i(text_su, 0);
            glUniformMatrix4fv(proj_su, 1, GL_FALSE, (const GLfloat *) &persp);
            glUniformMatrix4fv(view_su, 1, GL_FALSE, (const GLfloat *) &view);
            test.dispatch();

            ctl_panel.next();
            frame++;
            glfwPollEvents();

            flush_gl_errors();
        }
    }
}