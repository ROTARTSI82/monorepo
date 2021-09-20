#include "cel/game/etc/hanoi.hpp"
#include "cel/eng/log.hpp"

#include <cel/eng/misc_util.hpp>
#include <cel/app.hpp>

#include <imgui/imgui.h>
#include <fmt/format.h>
#include <numeric>

namespace cel::etc {


    hanoi_layer::hanoi_layer(world_t &w) : call(4096, w.parent->qvbo), wrld(&w), tex(w.parent->tex_pool.new_tex("dbg/test.png")) {
        call.num_blits = 3 + 5;
        call.instances[0] = draw_instance{gl_mat2::transform({10, 500}, 0), vec3{-500, 0, -2}, 1, vec2{0, 0}, vec2{1, 1}, vec2{2, 3}};
        call.instances[1] = draw_instance{gl_mat2::transform({10, 500}, 0), vec3{0, 0, -2}, 1, vec2{0, 0}, vec2{1, 1}, vec2{2, 3}};
        call.instances[2] = draw_instance{gl_mat2::transform({10, 500}, 0), vec3{500, 0, -2}, 1, vec2{0, 0}, vec2{1, 1}, vec2{2, 3}};
        load_disks(5);

        {
            flush_al_error("pre thing");
            auto src = w.parent->snd.music.borrow_one();
            flush_al_error("borrow");
            mb300 = w.parent->snd.new_file_buf("dbg/300MB.ogg");
            flush_al_error("new buf");
            src->enqueue_buf(mb300.get());
            // src->bind_buf(mb300.get());
            // flush_al_error("bind");
            // src->set_gain(1.0f);
            // flush_al_error("gain");
            // src->set_pitch(1.0f);
            // flush_al_error("pitch");
            // src->set_vel({0, 0, 0}); flush_al_error("vel");
            // src->set_pos({0, 0, 0}); flush_al_error("pos");
            // src->set_looping(false); flush_al_error("loop");
            src->play();
            flush_al_error("play");
        }
        wrld->parent->snd.cycle_gc();
    }

    void hanoi_layer::load_disks(int n) {
        src.resize(n);
        std::iota(src.rbegin(), src.rend(), 3);

        dst.clear();
        aux.clear();

        int w = 0;
        for (int i = 0; i < n; i++) {
            call.instances[3 + i] = draw_instance{gl_mat2::transform({50 + ++w * 20.0f, 40}, 0), vec3{-250, 0, -1}, 1, vec2{0, 0}, vec2{1, 1}, vec2{1, 1}};
        }
    }

    void hanoi_layer::enqueue_disks(const std::vector<uint8_t> &vec, float x) {
        float y = -500;
        for (const auto i : vec) {
            call.instances[i].center = vec3{x, y, -1};
            y += 80;
        }
    }

    void hanoi_layer::move(uint8_t f, uint8_t t) {
        CEL_INFO("MOVE {} -> {}", f, t);
        std::vector<uint8_t> *fv, *tv;
        switch (f) {
            case 0:
                fv = &src; break;
            case 1:
                fv = &aux; break;
            case 2:
                fv = &dst; break;
        }

        switch (t) {
            case 0:
                tv = &src; break;
            case 1:
                tv = &aux; break;
            case 2:
                tv = &dst; break;
        }

        from_y = -500 + 80.0f * (fv->size() - 1);
        to_y = -500 + 80.0f * tv->size();

        animated_block = fv->back();
        tv->push_back(fv->back());
        fv->pop_back();

        anim_from = f;
        anim_to = t;
        anim_progress = 0;

        // set to_y and from_y
    }

    void hanoi_layer::solve_recursively(uint8_t n, uint8_t f, uint8_t t) {
        if (n == 0) return;
        solve_recursively(n - 1, f, other(f, t));
        solution.emplace_back(f, t);
        solve_recursively(n - 1, other(f, t), t);
    }

    void hanoi_layer::tick(const input_frame &inp) {
        
    }

    void hanoi_layer::upload() {
        call.upload();
    }

    void hanoi_layer::draw() {
        wrld->parent->default_shaders.use();
        static const gl_mat4 proj = gl_mat4::ortho(-1000, 1000, -1000, 1000, 0.1, 100);
        glActiveTexture(GL_TEXTURE0);
        glUniformMatrix4fv(wrld->parent->su_proj, 1, GL_FALSE, (const GLfloat *) &proj);
        glUniformMatrix4fv(wrld->parent->su_view, 1, GL_FALSE, (const GLfloat *) &gl_mat4::ident);
        glUniform1i(wrld->parent->su_tex, 0);
        tex->bind();
        call.dispatch();

        ImGui::Begin("Hanoi Layer");
        if (ImGui::SliderInt("Num Discs", &n, 3, 8) || ImGui::Button("Reset")) {
            load_disks(n);
            call.num_blits = 3 + n;
        }

        if (ImGui::Button("Solve")) {
            load_disks(n);
            call.num_blits = 3 + n;

            solution.clear();
            solve_recursively(n, 0, 2);
            solution_iterator = 0;
        }

        if (solution_iterator != -1 && animated_block == 99) {
            auto res = solution.at(solution_iterator++);
            move(res.f, res.t);
            if (solution_iterator >= solution.size()) {
                solution_iterator = -1;
            }
        }

        ImGui::SliderFloat("Animation Speed", &speed, 0.01, 0.2);

        if (ImGui::Button("Src->Aux"))
            move(0, 1);
        ImGui::SameLine();
        if (ImGui::Button("Src->Dst"))
            move(0, 2);
        ImGui::SameLine();
        if (ImGui::Button("Aux->Src"))
            move(1, 0);
        ImGui::SameLine();
        if (ImGui::Button("Aux->Dst"))
            move(1, 2);
        ImGui::SameLine();
        if (ImGui::Button("Dst->Src"))
            move(2, 0);
        ImGui::SameLine();
        if (ImGui::Button("Dst->Aux"))
            move(2, 1);

        ImGui::End();
    }

    void hanoi_layer::enqueue_render() {
        enqueue_disks(src, -500);
        enqueue_disks(aux, 0);
        enqueue_disks(dst, 500);

        // animated one here
        if (animated_block != 99) {
            switch (stage) {
            case anim_stage::lift_up:
                call.instances[animated_block].center = vec3{lerp(vec2{-500 + 500.0f * anim_from, from_y}, vec2{-500 + 500.0f * anim_from, 750}, anim_progress), -1};
                if (anim_progress > 1) {
                    anim_progress = 0;
                    stage = anim_stage::slide_across;
                }
                break;
            case anim_stage::slide_across:
                call.instances[animated_block].center = vec3{lerp(vec2{-500 + 500.0f * anim_from, 750}, vec2{-500 + 500.0f * anim_to, 750}, anim_progress), -1};
                if (anim_progress > 1) {
                    anim_progress = 0;
                    stage = anim_stage::drop_down;
                }
                break;
            case anim_stage::drop_down:
                call.instances[animated_block].center = vec3{lerp(vec2{-500 + 500.0f * anim_to, 750}, vec2{-500 + 500.0f * anim_to, to_y}, anim_progress), -1};
                if (anim_progress > 1) {
                    anim_progress = 0;
                    stage = anim_stage::lift_up;
                    animated_block = 99;
                }
                break;
            }
            anim_progress += speed;
        }
    }
}

