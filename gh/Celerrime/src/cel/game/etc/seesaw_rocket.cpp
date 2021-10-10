#include "cel/game/etc/seesaw_rocket.hpp"
#include "cel/eng/log.hpp"

#include <cel/eng/misc_util.hpp>
#include <cel/app.hpp>

#include <imgui/imgui.h>
#include <fmt/format.h>
#include <numeric>

namespace cel::etc {


    ssrocket_layer::ssrocket_layer(world_t &w) : call(4096, w.app->qvbo), wrld(&w), rocket_tex(w.app->tex_pool.new_tex("dbg/rocket.png")) {
        call.num_blits = 3 + 5;
        call.instances[0] = draw_instance{gl_mat2::transform({20, 10}, 0), vec3{-500, 0, -2}, 1, vec2{0, 0}, vec2{1, 0.5}, vec2{1, 1}};
        call.instances[1] = draw_instance{gl_mat2::transform({10, 500}, 0), vec3{0, 0, -2}, 1, vec2{0, 0.5}, vec2{0.5, 0.5}, vec2{1, 1}};
        call.instances[2] = draw_instance{gl_mat2::transform({10, 500}, 0), vec3{500, 0, -2}, 1, vec2{0, 0.5}, vec2{0.5, 0.5}, vec2{1, 1}};
        call.upload();
    }

    void ssrocket_layer::tick(const input_frame &inp) {
        
    }

    void ssrocket_layer::upload() {
        
    } 

    void ssrocket_layer::draw() {
        int count;
        const float* axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1, &count);

        float right_thrust = (axes[5] + 1) * rocket.max_thrust / 2;
        float left_thrust = (axes[2] + 1) * rocket.max_thrust / 2;
        float rotation_inertia = 25; // rocket.thruster_mass; // * rocket.thruster_radius * rocket.thruster_radius;
        float torque = rocket.thruster_radius * right_thrust;
        torque -= rocket.thruster_radius * left_thrust;
        float angular_accel = torque / rotation_inertia;

        rocket.dtheta += angular_accel;
        rocket.theta += rocket.dtheta;
        rocket.fuel -= (left_thrust + right_thrust) * rocket.specific_impulse;

        vec2 accel = vec2{std::cos(rocket.theta), std::sin(rocket.theta)} * (left_thrust + right_thrust) / rocket.mass;
        rocket.vel += accel;
        rocket.vel.y -= gravity;
        rocket.pos += rocket.vel;

        left_thrust = (axes[2] + 1) / 2;
        right_thrust = (axes[5] + 1) / 2;

        ImGui::Begin("Rocket");

        if (ImGui::Button("Reset")) {
            rocket = rocket_t{};
        }
        
        #define monitor(x) ImGui::Text(fmt::format(#x " = {}", x).c_str())
        monitor(rocket.dtheta);
        monitor(rocket.theta);
        monitor(rocket.vel);
        monitor(rocket.pos);


        ImGui::End();

        call.num_blits = 3;

        wrld->app->default_shaders.use();
        glUniformMatrix4fv(wrld->app->su_proj, 1, GL_FALSE, (const GLfloat *) &projection);
        glActiveTexture(GL_TEXTURE0);
        glUniform1i(wrld->app->su_tex, 0);
        rocket_tex->bind();
        glUniformMatrix4fv(wrld->app->su_view, 1, GL_FALSE, (const GLfloat *) &mat4::ident);

        call.instances[0].center = vec3{rocket.pos, -1};
        call.instances[0].transform = mat2::transform({2, 1}, rocket.theta + consts_for<float>::pi() / 2);

        mat2 angle_mat = mat2::transform({1, 1}, rocket.theta + consts_for<float>::pi() / 2);

        call.instances[1].transform = mat2::transform({left_thrust, 1}, rocket.theta + consts_for<float>::pi() / 2);
        call.instances[1].center = vec3{rocket.pos + vec2{1, 1} * angle_mat, -0.5};

        call.instances[2].transform = mat2::transform({right_thrust, 1}, rocket.theta + consts_for<float>::pi() / 2);
        call.instances[2].center = vec3{rocket.pos + vec2{-1, 1} * angle_mat, -0.5};

        call.upload();
        call.dispatch();

        // 5 right
        // 2 left
        // -1 = released, 1 =
    }

    void ssrocket_layer::enqueue_render() {
        
    }
}

