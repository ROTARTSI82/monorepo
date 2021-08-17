#include "cel/game/dbg/phys_test.hpp"

#include <cel/eng/misc_util.hpp>
#include <cel/app.hpp>

#include <cel/eng/log.hpp>

#include <imgui/imgui.h>
#include <fmt/format.h>

namespace cel::dbg {
    physics_test_layer::physics_test_layer(world_t &w) : draw_info(4096, w.parent->qvbo), sq_tex{w.parent->opt.res_path("dbg/test.png")} {
        shader frag{GL_FRAGMENT_SHADER, read_entire_file(w.parent->opt.res_path("frag/default.frag"))};
        shader vert{GL_VERTEX_SHADER, read_entire_file(w.parent->opt.res_path("vert/default.vert"))};

        shaders.attach(frag.get_id());
        shaders.attach(vert.get_id()); 
        shaders.link();

        draw_info.instances[0] = draw_instance{gl_mat2{identity_tag}, {0, 0, -5}, 1, {0, 0}, {1, 0.5}, {1, 1}};
        draw_info.instances[1] = draw_instance{gl_mat2{identity_tag}, {0, 0, -5}, 1, {0, 0}, {1, 0.5}, {1, 1}};
        draw_line(draw_info.instances[2], b.center, b.center + b.axes*2, depth + 1);
        draw_line(draw_info.instances[3], b.center, b.center - b.axes*2, depth + 1);
        draw_line(draw_info.instances[4], b.center, b.center + vec2{b.axes.x, -b.axes.y}*2, depth + 1);
        draw_line(draw_info.instances[5], b.center, b.center + vec2{-b.axes.x, b.axes.y}*2, depth + 1);

        for (int i = 2; i < 7; i++) {
            draw_info.instances[i].alpha_mult = 1;
            draw_info.instances[i].sample_extent = vec2{1, 0.5};
            draw_info.instances[i].sample_origin = vec2{0, 0.5};
            draw_info.instances[i].sample_ntiles = vec2{1, 1};
        }


        draw_info.num_blits = 2 + 5;
        draw_info.upload();

        proj_su = shaders.get_uniform("projection_mat");
        view_su = shaders.get_uniform("view_mat");

        glUniform1i(shaders.get_uniform("tex"), 0);
    }

    void physics_test_layer::tick(const input_frame &inp) {
        a.center -= inp.direction * -0.25;

        std::tie(push, status) = a.collide_direction(b);
        if (status & (1<<1)) {
            a.center += push;
        }

    }

    void physics_test_layer::upload() {
        draw_info.upload();
    }

    void physics_test_layer::enqueue_render() {
        draw_info.instances[0].center = vec3{a.center, depth};
        draw_info.instances[0].transform.c0.x = a.axes.x;
        draw_info.instances[0].transform.c1.y = a.axes.y;
        draw_info.instances[1].center = vec3{b.center, depth};
        draw_info.instances[1].transform.c0.x = b.axes.x;
        draw_info.instances[1].transform.c1.y = b.axes.y;

        draw_info.instances[0].sample_origin = vec2{0, a.collides_with(b) ? 0.5f : 0.0f};

        draw_line(draw_info.instances[6], b.center, a.center, depth + 0.01);
        draw_line(draw_info.instances[2], b.center, b.center + b.axes*2, depth + 0.01);
        draw_line(draw_info.instances[3], b.center, b.center - b.axes*2, depth + 0.01);
        draw_line(draw_info.instances[4], b.center, b.center + vec2{b.axes.x, -b.axes.y}*2, depth + 0.01);
        draw_line(draw_info.instances[5], b.center, b.center + vec2{-b.axes.x, b.axes.y}*2, depth + 0.01);
    }

    void physics_test_layer::draw() {
        shaders.use();

        glActiveTexture(GL_TEXTURE0);
        sq_tex.bind();
        glUniformMatrix4fv(proj_su, 1, GL_FALSE, (const GLfloat *) &proj_mat);
        glUniformMatrix4fv(view_su, 1, GL_FALSE, (const GLfloat *) &view_mat);
        draw_info.dispatch();

        ImGui::Begin((fmt::format("physics_test_layer@{:#016x}", reinterpret_cast<uint64_t>(this))).c_str());
        ImGui::Text("View Matrix: %s", to_string(view_mat).c_str());
        ImGui::Text("Proj Matrix: %s", to_string(proj_mat).c_str());
        ImGui::Text("Status: %i", status);
        ImGui::Text("A @ %s", to_string(a.center).c_str());
        ImGui::Text("B @ %s", to_string(b.center).c_str());
        ImGui::SliderFloat("Depth", &depth, -50, 0);

        ImGui::Text("Stat %i - Push: %s", status, to_string(push).c_str());


        ImGui::End();
    }
}