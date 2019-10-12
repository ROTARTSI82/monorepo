//
// Created by Grant on 10/1/19.
//

#include "mason/rigidbody.h"

namespace mason {

    rigidbody::rigidbody(mason::transform_info trans, glm::vec3 vel, glm::vec3 accel, float mass) : transforms(trans),
                                                                                                    velocity(vel),
                                                                                                    acceleration(accel),
                                                                                                    mass(mass) {
        last_update = std::chrono::high_resolution_clock::now();
    }

    void rigidbody::clear_acceleration() {
        acceleration = glm::vec3(0);
    }

    void rigidbody::push_force(glm::vec3 force) {
        acceleration += force / mass;
    }

    void rigidbody::apply_acceleration() {
        auto now = std::chrono::high_resolution_clock::now();
        auto diff = last_update - now;

        // Getting the delta time in seconds
        float time = std::chrono::duration_cast<std::chrono::duration<float, std::ratio<1>>>(diff).count();
        last_update = now;

        // Consider time passed when applying velocity and acceleration.
        velocity += acceleration * time;
        transforms.position += velocity * time;
    }

    void rigidbody::apply_friction(float coefficient) {
        // Equivalent to `push_force(-velocity * coefficient)`
        acceleration += (-velocity * coefficient) / mass;
    }
}
