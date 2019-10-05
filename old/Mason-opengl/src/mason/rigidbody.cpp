//
// Created by Grant on 10/1/19.
//

#include "mason/rigidbody.h"

namespace mason {

    rigidbody::rigidbody(mason::transform_info trans, glm::vec3 vel, glm::vec3 accel, float mass) : transforms(trans),
                                                                                                    velocity(vel),
                                                                                                    acceleration(accel),
                                                                                                    mass(mass) {}

    void rigidbody::clear_acceleration() {
        acceleration = glm::vec3(0);
    }

    void rigidbody::push_force(glm::vec3 force) {
        acceleration += force / mass;
    }

    void rigidbody::apply_acceleration() {
        velocity += acceleration;
        transforms.position += velocity;
    }

    void rigidbody::apply_friction(float coefficient) {
        // Equivalent to `push_force(-velocity * coefficient)`
        acceleration += (-velocity * coefficient) / mass;
    }
}
