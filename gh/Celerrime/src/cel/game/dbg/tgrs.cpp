#include "cel/game/dbg/tgrs.hpp"

#include <cel/eng/misc_util.hpp>
#include <cel/app.hpp>

#include <cel/eng/log.hpp>

#include <imgui/imgui.h>
#include <fmt/format.h>
#include <random>

namespace cel::dbg {
    tgrs_layer::tgrs_layer(world_t &w) : parent(w), draw_info(4096, w.parent->qvbo), sq_tex{w.parent->tex_pool.new_tex("dbg/test.png")} {
        shader frag{GL_FRAGMENT_SHADER, read_entire_file(w.parent->opt.res_path("frag/default.frag"))};
        shader vert{GL_VERTEX_SHADER, read_entire_file(w.parent->opt.res_path("vert/default.vert"))};

        shaders.attach(frag.get_id());
        shaders.attach(vert.get_id()); 
        shaders.link();

        shaders.use();
        gl_mat4 view_mat = mat4::transform({-400, -300, -300}, {1, 1}, 0);
        view_mat = mat4{identity_tag};
        view_mat = mat4::transform({0, 0, -1}, {1, 1}, 0);
        gl_mat4 proj_mat = mat4::perspective(90.0, 4.0 / 3, 0.1, 100.0);
        proj_mat = mat4::ortho(0, 800, 600, 0, 0.1, 100);

        glUniform1i(shaders.get_uniform("tex"), 0);
        glUniformMatrix4fv(shaders.get_uniform("projection_mat"), 1, GL_FALSE, (const GLfloat *) &proj_mat);
        glUniformMatrix4fv(shaders.get_uniform("view_mat"), 1, GL_FALSE, (const GLfloat *) &view_mat);

        draw_info.instances[0] = draw_instance{gl_mat2{{10, 0}, {0, 10}}, vec3{player.center, 0}, 1, {0, 0}, {1, 0.25}, {1, 1}};
        draw_info.instances[1] = draw_instance{gl_mat2{{25, 0}, {0, 50}}, vec3{}, 1, {0, 0.25}, {1, 0.25}, {1, 1}};
        load_map(0);
    }

    void tgrs_layer::load_map(int n) {
        finish.center = lvl_finishes[n] + vec2{25, 50};
        draw_info.instances[1].center = vec3{finish.center, 0};
        
        int ind = 2;

        for (const auto &b : lvl_platforms[n]) {
            draw_info.instances[ind++] = draw_instance{gl_mat2{{b.axes.x, 0}, {0, b.axes.y}}, vec3{b.center, 0}, 1, {0, 0.5}, {1, 0.25}, {1, 1}};
        }

        for (const auto &b : lvl_spikes[n]) {
            draw_info.instances[ind++] = draw_instance{gl_mat2{{b.axes.x, 0}, {0, b.axes.y}}, vec3{b.center, 0}, 1, {0, 0.75}, {1, 0.25}, {1, 1}};
        }


        std::random_device r;
        std::default_random_engine eng(r());
        std::uniform_int_distribution<int> y_dist(0, 600);
        std::uniform_int_distribution<int> x_dist(0, 800);
        for (int i = 0; i < 25; i++) {
            draw_info.instances[ind++] = draw_instance{gl_mat2{identity_tag}, vec3{(float) x_dist(eng), (float) y_dist(eng), 0}, 1, {0, 0.5}, {1, 0.25}, {1, 1}};
        }

        draw_info.num_blits = ind;
    }

    void tgrs_layer::tick(const input_frame &inp) {
        // vel.clamp_magn_in_place(5);
        vel.x = std::max(std::min(vel.x, 5.0f), -5.0f);
        player.center += vel;
        
        vel += vec2{0, 0.3}; // gravity
        bool apply_friction = inp.direction.magnitude() < 0.25;

        grounded = false;
        float mark = -999999;
        for (const auto &box : lvl_platforms[level]) {
            auto [ push, status ] = player.collide_direction(box);
            if (status) { 
                vec2 candidate = push.normalized();
                float d = vec2::dot(candidate, vec2{0, -1});
                if (d > mark) {
                    ground_normal = candidate;
                    mark = d;
                }

                grounded = true;
                if (true) {
                    vec2 target = candidate * vec2::dot(candidate, vel);
                    vec2 diff = target - vel;
                    vel += diff * 0.1;
                }
            }

            if (status & (1<<1)) {
                player.center += push;
                vec2 rot = vec2{-push.y, push.x}.normalized(); // rotate 90deg counterclockwise
                vel = rot * vec2::dot(vel, rot);
            }
        }

        if (inp.buttons[(int) button_id::jump] && grounded && !parent.prev_inp.buttons[(int) button_id::jump]) {
            CEL_TRACE("JMP norm = {}", to_string(ground_normal));
            jump_frames = 30;
            jmp_norm = ground_normal;
            vel += ground_normal * 3;
        } else if (!inp.buttons[(int) button_id::jump]) {
            jump_frames = 0;
        } else if (--jump_frames > 0) {
            vel += jmp_norm;
            jmp_norm *= 0.9;
        }

        vel.x += inp.direction.x;


        if (player.center.x < 10.0f) {
            player.center.x = 10;
            vel.x = 0;
        } else if (player.center.x > 790) {
            player.center.x = 790;
            vel.x = 0;
        }

        if (player.center.y - 10 < 0) {
            player.center.y = 11;
            vel = vec2{1, 0} * vec2::dot(vel, vec2{1, 0});
        }

        for (const auto &box : lvl_spikes[level]) {
            if (player.collides_with(box)) {
                player.center = {40, 60};
                vel = {0, 0};
            }
        }

        if (player.collides_with(finish)) {
            load_map(++level);
            player.center = {40, 60};
            vel = {0, 0};
        }

         if (player.center.y > 600) {
            player.center = {40, 60};
            vel = {0, 0};
        }

    }

    void tgrs_layer::upload() {
        draw_info.upload();
    }

    void tgrs_layer::enqueue_render() {
        draw_info.instances[0].center = vec3{player.center, 0};
    }

    void tgrs_layer::draw() {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        shaders.use();

        glActiveTexture(GL_TEXTURE0);
        sq_tex->bind();
        draw_info.dispatch();
    }
}