//
// Created by grant on 5/31/21.
//

#include <string.h>
#include "phys.h"
#include "math.h"

#include "common.h"
#include "config.h"

#include <float.h>

// Collision detection using the hyperplane separation theorem. Probably overkill for simple rects but this was
// the first method i found.
// https://en.wikipedia.org/wiki/Hyperplane_separation_theorem
// dyn should be the object that we want to push out of stat
uint8_t collides_with(phys_obj_t *dyn, phys_obj_t *stat, push_info_t *out_info) {
    out_info->is_true_collision = 1;
    vec2 possible_axes[4];

    possible_axes[0] = dyn->trans.form.c0; // 0 = dyn's semi-major x axis
    possible_axes[1] = dyn->trans.form.c1; // 1 = dyn's semi-major y axis
    possible_axes[2] = stat->trans.form.c0; // 2 = stat's semi-major x axis
    possible_axes[3] = stat->trans.form.c1; // 3 = stat's semi-major y axis


    vec2 vertices[8]; // [0, 3] = verts of dyn, [4, 7] = verts of stat
    {  // this can probably be optimized a ton

        vertices[0] = v2_add(&dyn->trans.late.v2, &possible_axes[0]);
        v2_add_eq(&vertices[0], &possible_axes[1]);

        vertices[1] = v2_add(&dyn->trans.late.v2, &possible_axes[0]);
        v2_sub_eq(&vertices[1], &possible_axes[1]);

        vertices[2] = v2_sub(&dyn->trans.late.v2, &possible_axes[0]);
        v2_add_eq(&vertices[2], &possible_axes[1]);

        vertices[3] = v2_sub(&dyn->trans.late.v2, &possible_axes[0]);
        v2_sub_eq(&vertices[3], &possible_axes[1]);


        vertices[4] = v2_add(&stat->trans.late.v2, &possible_axes[2]);
        v2_add_eq(&vertices[4], &possible_axes[3]);

        vertices[5] = v2_add(&stat->trans.late.v2, &possible_axes[2]);
        v2_sub_eq(&vertices[5], &possible_axes[3]);

        vertices[6] = v2_sub(&stat->trans.late.v2, &possible_axes[2]);
        v2_add_eq(&vertices[6], &possible_axes[3]);

        vertices[7] = v2_sub(&stat->trans.late.v2, &possible_axes[2]);
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
        FLOAT_T d_min = FLT_MAX, d_max = FLT_MIN, s_min = FLT_MAX, s_max = FLT_MIN;
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

        // CEL_COLLIDE_THRESHOLD makes it harder for this to be true
        if ((s_min - CEL_COLLIDE_THRESHOLD) > d_max || (s_max + CEL_COLLIDE_THRESHOLD) < d_min) { // only 2 cases where sections aren't overlapping
            return 0;
        }

        // s_min < d_max && s_max > d_min is a DeMorgan of the above condition. It is true if they are really colliding, even without CEL_COLLIDE_THRESHOLD
        // This sets out_info to false if they are not really colliding and the above condition was only false because of CEL_COLLIDE_THRESHOLD.
        out_info->is_true_collision &= s_min < d_max && s_max > d_min;

        if (i > 1) { // static axes
            out_info->axes[i - 2].pos_push = s_max - d_min;
            out_info->axes[i - 2].neg_push = s_min - d_max;
            out_info->axes[i - 2].axis = possible_axes[i]; // this will store the unit vector
        }
    }

    return 1;
}

void handle_collision(dyn_phys_obj_t *dynamic, phys_obj_t *static_obj) {
    push_info_t push_info;
    // &dynamic->sup == (phys_obj_t *) dynamic
    if (!collides_with(&dynamic->sup, static_obj, &push_info)) {
        return;
    }

    vec2 delta = v2_sub(&dynamic->sup.trans.late.v2, &static_obj->trans.late.v2);
    FLOAT_T target_theta = atan2f(delta.y, delta.x);
    if (target_theta < 0) {target_theta += PI * 2.0f; } // just add and fmodf?

    vec2 v_1_1 = v2_add(&static_obj->trans.form.c0, &static_obj->trans.form.c1); // 1, 1
    vec2 v_n1_1 = v2_sub(&static_obj->trans.form.c1, &static_obj->trans.form.c0); // -1, 1

    // TODO: Optimize this fucking nightmare
    FLOAT_T theta_1_1 = atan2f(v_1_1.y, v_1_1.x);
    FLOAT_T theta_n1_1 = atan2f(v_n1_1.y, v_n1_1.x);

    // we can get rid of the `if` by just adding PI * 2 and using fmodf
    // if `if` becomes a performance issue.
    if (theta_1_1 < 0) {theta_1_1 += PI * 2.0f;}
    if (theta_n1_1 < 0) {theta_n1_1 += PI * 2.0f;}

    FLOAT_T theta_n1_n1 = fmodf(PI + theta_1_1, PI * 2.0f);
    FLOAT_T theta_1_n1 = fmodf(PI + theta_n1_1, PI * 2.0f);

    PRINT("(1,1) = %f, (-1,1) = %f, (-1,-1) = %f, (1,-1) = %f\n", theta_1_1, theta_n1_1, theta_n1_n1, theta_1_n1);

    uint8_t axis;
    float push_scalar;
    if ((target_theta > theta_1_1 && target_theta < theta_n1_1) || (target_theta > theta_n1_n1 && target_theta < theta_1_n1)) { // y axis
        push_scalar = fabsf(push_info.axes[1].pos_push) > fabsf(push_info.axes[1].neg_push) ? push_info.axes[1].neg_push : push_info.axes[1].pos_push;
        axis = 1;
    } else { // x axis
        push_scalar = fabsf(push_info.axes[0].pos_push) > fabsf(push_info.axes[0].neg_push) ? push_info.axes[0].neg_push : push_info.axes[0].pos_push;
        axis = 0;
    }

    // push_info.axes[axis].axis is a normalized unit vector!!
    vec2 push = v2_mults(&push_info.axes[axis].axis, push_scalar);

    if (!push_info.is_true_collision) {
        // TODO: Handle "grounded" logic here!
        return;
    }

    v2_add_eq(&dynamic->sup.trans.late.v2, &push); // push the dynamic object out of the wall.

    // Don't check if (v2_dot(&push, &dynamic->vel) < 0); possibly opens to door for some exploits

    {
        vec2 zero;
        zero.x = 0;
        zero.y = 0;

        push = push_scalar > 0 ? push_info.axes[axis].axis : v2_sub(&zero, &push_info.axes[axis].axis);
    } // or just normalize normally

    // Set push to the unit normal vector rotated 90deg counterclockwise
    FLOAT_T tmp_x = push.x;
    push.x = -push.y;
    push.y = tmp_x;

    vec2 new_vel = v2_mults(&push, v2_dot(&push, &dynamic->vel));
    vec2 delta_vel = v2_sub(&new_vel, &dynamic->vel); // this should point in the same direction as the normal.
    v2_mults_eq(&delta_vel, dynamic->sup.bounciness * static_obj->bounciness + 1); // bounciness scales the additional bounce.
    v2_add_eq(&dynamic->vel, &delta_vel);

    // &dynamic->sup should be equal to dynamic
    dynamic->sup.collide_callback(&dynamic->sup, static_obj, &delta_vel, 0); // TODO: Null checks?
    static_obj->collide_callback(static_obj, &dynamic->sup, &delta_vel, 1);
}

