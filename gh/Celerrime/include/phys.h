//
// Created by grant on 5/31/21.
//

#pragma once

#ifndef GAMETEST_PHYS_H
#define GAMETEST_PHYS_H

#include "linalg.h"
#include "stdint.h"

typedef struct {
    // should be a pointer to the smat4 from draw_instance_t.
    // draw_instance_t needs it because it has to be sent to the GPU
    // 2D translation, rotation, and scaling data can be easily retrieved:
    //    2x2 matrix in the top left contains the semi-major x and y axes of the square
    //    first two rows of last column contains the 2d translation
    smat4 *trans;

    vec2 vel;
    vec2 accl;

    FLOAT_T mass;
} phys_obj_t;

uint8_t collides_with(phys_obj_t *a, phys_obj_t *b);
void handle_collisions(phys_obj_t *dynamic, phys_obj_t *static_objs, int num_static);

#endif //GAMETEST_PHYS_H
