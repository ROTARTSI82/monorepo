//
// Created by grant on 5/31/21.
//

#pragma once

#ifndef GAMETEST_PHYS_H
#define GAMETEST_PHYS_H

#include "linalg.h"
#include "stdint.h"

struct phys_obj_t;
typedef struct phys_obj_t {
    // should be a pointer to the smat4 from draw_instance_t.
    // draw_instance_t needs it because it has to be sent to the GPU
    // 2D translation, rotation, and scaling data can be easily retrieved:
    //    2x2 matrix in the top left contains the semi-major x and y axes of the square
    //    first two rows of last column contains the 2d translation
    smat2 *transform;
    vec2 *translation;

    vec2 vel;
    FLOAT_T mass;

    void *data;
    void (*collide_callback)(struct phys_obj_t *other, vec2 *deltVel, uint8_t is_static);

    struct phys_obj_t *reference_frame; // the object we are "grounded" on
    // FLOAT_T bounce_multiplier, traction_multiplier;
} phys_obj_t;

typedef struct push_info_t {
    struct projection_info_t {
        FLOAT_T pos_push, neg_push;
        vec2 axis; // unit vector of axis
    } projection_info_t;

    // axis 0 is the projection onto the static element's x axis, axis 1 is the y axis
    struct projection_info_t axes[2];
    uint8_t is_true_collision; // to be used for grounded calculations
} push_info_t;

uint8_t collides_with(phys_obj_t *dyn, phys_obj_t *stat, push_info_t *out_info);
void handle_collisions(phys_obj_t *dynamic, phys_obj_t *static_objs, int num_static);

#endif //GAMETEST_PHYS_H
