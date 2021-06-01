//
// Created by grant on 5/31/21.
//

#include <string.h>
#include "phys.h"
#include "math.h"


// Collision detection using the hyperplane separation theorem. Probably overkill for simple rects but this was
// the first method i found.
// https://en.wikipedia.org/wiki/Hyperplane_separation_theorem
uint8_t collides_with(phys_obj_t *a, phys_obj_t *b) {
    vec2 *possible_axes[4];

    // should be safe from strict aliasing because we only access it through possible_axes and never a->trans
    possible_axes[0] = (vec2 *) &a->trans->c0; // 0 = a's semi-major x axis
    possible_axes[1] = (vec2 *) &a->trans->c1; // 1 = a's semi-major y axis
    possible_axes[2] = (vec2 *) &b->trans->c0; // 2 = b's semi-major x axis
    possible_axes[3] = (vec2 *) &b->trans->c1; // 3 = b's semi-major y axis

    vec2 vertices[8]; // [0, 3] = verts of a, [4, 7] = verts of b
    {  // this can probably be optimized a ton
        vec2 *a_center = (vec2 *) &a->trans->c3;
        vec2 *b_center = (vec2 *) &b->trans->c3;

        vertices[0] = v2_add(a_center, possible_axes[0]);
        v2_add_eq(&vertices[0], possible_axes[1]);

        vertices[1] = v2_add(a_center, possible_axes[0]);
        v2_sub_eq(&vertices[1], possible_axes[1]);

        vertices[2] = v2_sub(a_center, possible_axes[0]);
        v2_add_eq(&vertices[2], possible_axes[1]);

        vertices[3] = v2_sub(a_center, possible_axes[0]);
        v2_sub_eq(&vertices[3], possible_axes[1]);


        vertices[4] = v2_add(b_center, possible_axes[2]);
        v2_add_eq(&vertices[4], possible_axes[3]);

        vertices[5] = v2_add(b_center, possible_axes[2]);
        v2_sub_eq(&vertices[5], possible_axes[3]);

        vertices[6] = v2_sub(b_center, possible_axes[2]);
        v2_add_eq(&vertices[6], possible_axes[3]);

        vertices[7] = v2_sub(b_center, possible_axes[2]);
        v2_sub_eq(&vertices[7], possible_axes[3]);
    }


    // THIS IS NOT OPTIMISED AT ALL
    for (int i = 0; i < 4; i++) {
        FLOAT_T a_min = 999, a_max = -999, b_min = 999, b_max = -999;
        for (int j = 0; j < 4; j++) { // verts of a
            FLOAT_T v = v2_dot(possible_axes[i], &vertices[j]);

            a_max = fmaxf(a_max, v);
            a_min = fminf(a_min, v);
        }

        for (int j = 4; j < 8; j++) {
            FLOAT_T v = v2_dot(possible_axes[i], &vertices[j]);

            b_max = fmaxf(b_max, v);
            b_min = fminf(b_min, v);
        }

        if (b_min > a_max || b_max < a_min) { // only 2 cases where sections aren't overlapping
            return 0;
        }
    }

    return 1;
}

void handle_collisions(phys_obj_t *dynamic, phys_obj_t *static_objs, int num_static) {
    for (int i = 0; i < num_static; i++) {
        if (!collides_with(dynamic, static_objs + i)) {
            continue; // no collision to handle
        }

        // TODO: implement
    }
}

