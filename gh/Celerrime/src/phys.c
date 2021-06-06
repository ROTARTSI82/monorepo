//
// Created by grant on 5/31/21.
//

#include <string.h>
#include "phys.h"
#include "math.h"

#include "common.h"


// Collision detection using the hyperplane separation theorem. Probably overkill for simple rects but this was
// the first method i found.
// https://en.wikipedia.org/wiki/Hyperplane_separation_theorem
// dyn should be the object that we want to push out of stat
uint8_t collides_with(phys_obj_t *dyn, phys_obj_t *stat, push_info_t *out_info) {
    vec2 possible_axes[4];

    // should be safe from strict aliasing because we only access it through possible_axes and never dyn->trans
    possible_axes[0] = dyn->trans->c0.v2; // 0 = dyn's semi-major x axis
    possible_axes[1] = dyn->trans->c1.v2; // 1 = dyn's semi-major y axis
    possible_axes[2] = stat->trans->c0.v2; // 2 = stat's semi-major x axis
    possible_axes[3] = stat->trans->c1.v2; // 3 = stat's semi-major y axis


    vec2 vertices[8]; // [0, 3] = verts of dyn, [4, 7] = verts of stat
    {  // this can probably be optimized a ton
        vec2 *d_center = &dyn->trans->c3.v2;
        vec2 *s_center = &stat->trans->c3.v2;

        vertices[0] = v2_add(d_center, &possible_axes[0]);
        v2_add_eq(&vertices[0], &possible_axes[1]);

        vertices[1] = v2_add(d_center, &possible_axes[0]);
        v2_sub_eq(&vertices[1], &possible_axes[1]);

        vertices[2] = v2_sub(d_center, &possible_axes[0]);
        v2_add_eq(&vertices[2], &possible_axes[1]);

        vertices[3] = v2_sub(d_center, &possible_axes[0]);
        v2_sub_eq(&vertices[3], &possible_axes[1]);


        vertices[4] = v2_add(s_center, &possible_axes[2]);
        v2_add_eq(&vertices[4], &possible_axes[3]);

        vertices[5] = v2_add(s_center, &possible_axes[2]);
        v2_sub_eq(&vertices[5], &possible_axes[3]);

        vertices[6] = v2_sub(s_center, &possible_axes[2]);
        v2_add_eq(&vertices[6], &possible_axes[3]);

        vertices[7] = v2_sub(s_center, &possible_axes[2]);
        v2_sub_eq(&vertices[7], &possible_axes[3]);
    }

    // now that vertices are calculated, we want the unit vector so that
    // the dot product is equal to the scalar projection
    v2_mults_eq(&possible_axes[0], 1.0f / v2_magnitude(&possible_axes[0]));
    v2_mults_eq(&possible_axes[1], 1.0f / v2_magnitude(&possible_axes[1]));
    v2_mults_eq(&possible_axes[2], 1.0f / v2_magnitude(&possible_axes[2]));
    v2_mults_eq(&possible_axes[3], 1.0f / v2_magnitude(&possible_axes[3]));


    // THIS IS NOT OPTIMISED AT ALL
    for (int i = 0; i < 4; i++) {
        FLOAT_T d_min = 999, d_max = -999, s_min = 999, s_max = -999;
        for (int j = 0; j < 4; j++) { // verts of dyn
            FLOAT_T v = v2_dot(&possible_axes[i], &vertices[j]);

            d_max = fmaxf(d_max, v);
            d_min = fminf(d_min, v);
        }

        for (int j = 4; j < 8; j++) {
            FLOAT_T v = v2_dot(&possible_axes[i], &vertices[j]);

            s_max = fmaxf(s_max, v);
            s_min = fminf(s_min, v);
        }

        if (s_min > d_max || s_max < d_min) { // only 2 cases where sections aren't overlapping
            return 0;
        }

        if (i > 1) { // static axes
            out_info->axes[i - 2].pos_push = s_max - d_min;
            out_info->axes[i - 2].neg_push = s_min - d_max;
            out_info->axes[i - 2].axis = possible_axes[i]; // this will store the unit vector
        }
    }

    return 1;
}

void handle_collisions(phys_obj_t *dynamic, phys_obj_t *static_objs, int num_static) {
    for (int i = 0; i < num_static; i++) {
        push_info_t push_info;
        if (!collides_with(dynamic, static_objs + i, &push_info)) {
            continue; // no collision to handle
        }

        vec2 delta = v2_sub(&dynamic->trans->c3.v2, &static_objs[i].trans->c3.v2);
        FLOAT_T target_theta = atan2f(delta.y, delta.x);
        if (target_theta < 0) {target_theta += PI * 2; }

        vec2 v_1_1 = v2_add(&static_objs[i].trans->c0.v2, &static_objs[i].trans->c1.v2); // 1, 1
        vec2 v_n1_1 = v2_sub(&static_objs[i].trans->c1.v2, &static_objs[i].trans->c0.v2); // -1, 1

        // TODO: Optimize this fucking nightmare
        FLOAT_T theta_1_1 = atan2f(v_1_1.y, v_1_1.x);
        FLOAT_T theta_n1_1 = atan2f(v_n1_1.y, v_n1_1.x);
        if (theta_1_1 < 0) {theta_1_1 += PI * 2;}
        if (theta_n1_1 < 0) {theta_n1_1 += PI * 2;}

        FLOAT_T theta_n1_n1 = PI + theta_1_1;
        FLOAT_T theta_1_n1 = PI + theta_n1_1;
        if (theta_n1_n1 > PI * 2) {theta_n1_n1 -= PI * 2;}
        if (theta_1_n1 > PI * 2) {theta_1_n1 -= PI * 2;}

        PRINT("(1,1) = %f, (-1,1) = %f, (-1,-1) = %f, (1,-1) = %f\n", theta_1_1, theta_n1_1, theta_n1_n1, theta_1_n1);

        vec2 push;
        if ((target_theta > theta_1_1 && target_theta < theta_n1_1) || (target_theta > theta_n1_n1 && target_theta < theta_1_n1)) { // y axis
            FLOAT_T y_push = fabsf(push_info.axes[1].pos_push) > fabsf(push_info.axes[1].neg_push) ? push_info.axes[1].neg_push : push_info.axes[1].pos_push;
            push = v2_mults(&push_info.axes[1].axis, y_push);
            v2_add_eq(&dynamic->trans->c3.v2, &push);
        } else { // x axis
            FLOAT_T x_push = fabsf(push_info.axes[0].pos_push) > fabsf(push_info.axes[0].neg_push) ? push_info.axes[0].neg_push : push_info.axes[0].pos_push;
            push = v2_mults(&push_info.axes[0].axis, x_push);
            v2_add_eq(&dynamic->trans->c3.v2, &push);
        }

        // normalize push vector for velocity change calculations
        v2_mults_eq(&push, 1.0f / v2_magnitude(&push));

        // Don't check if (v2_dot(&push, &dynamic->vel) < 0); More efficient and possibly opens to door for some exploits
        // Rotate push by 90deg counterclockwise
        FLOAT_T tmp_x = push.x;
        push.x = -push.y;
        push.y = tmp_x;

        vec2 new_vel = v2_mults(&push, v2_dot(&push, &dynamic->vel)); // TODO: Implement "bounciness" causing it to slightly overshoot?
        vec2 delta_vel = v2_sub(&new_vel, &dynamic->vel); // this should point in the same direction as the normal.

        dynamic->collide_callback(static_objs + i, &delta_vel, 0); // TODO: Null checks?
        static_objs[i].collide_callback(dynamic, &delta_vel, 1);
    }
}

