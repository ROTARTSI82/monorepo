//
// Created by grant on 5/29/21.
//

#include "linalg.h"

#include "common.h"

#include <stdio.h>
#include <string.h> // memset

const FLOAT_T sm3_identity[3][4] = {1, 0, 0, 0,
                                    0, 1, 0, 0,
                                    0, 0, 1, 0};

// these are extremely inefficient implementations but i'm too lazy to do it properly with assembly so eh

void v3_add_eq(vec3 *lhs, vec3 *rhs) {
    lhs->x += rhs->x;
    lhs->y += rhs->y;
    lhs->z += rhs->z;
}

void v3_mults_eq(vec3 *lhs, FLOAT_T scalar) {
    lhs->x *= scalar;
    lhs->y *= scalar;
    lhs->z *= scalar;
}

vec3 v3_multm(vec3 *a, smat3 *rhs) {
    smat3 crhs = *rhs; // compiler should generate memcpy for me

    vec3 ret;
    memset(&ret, 0, sizeof(ret)); // zero ret

    v3_mults_eq(&crhs.c0, a->x);
    v3_mults_eq(&crhs.c1, a->y);
    v3_mults_eq(&crhs.c2, a->z);

    v3_add_eq(&ret, &crhs.c0);
    v3_add_eq(&ret, &crhs.c1);
    v3_add_eq(&ret, &crhs.c2);

    return ret;
}


smat3 sm3_mult(smat3 *lhs, smat3 *rhs) {
    smat3 ret;
    ret.c0 = v3_multm(&lhs->c0, rhs);
    ret.c1 = v3_multm(&lhs->c1, rhs);
    ret.c2 = v3_multm(&lhs->c2, rhs);

    return ret;
};

void sm3_mult_eq(smat3 *lhs, smat3 *rhs) {
    v3_multm_eq(&lhs->c0, rhs);
    v3_multm_eq(&lhs->c1, rhs);
    v3_multm_eq(&lhs->c2, rhs);
};


vec2 v2_mults(vec2 *a, FLOAT_T scalar) {
    vec2 ret;
    ret.x = a->x * scalar;
    ret.y = a->y * scalar;
    return ret;
};

vec2 v2_add(vec2 *a, vec2 *b) {
    vec2 ret;
    ret.x = a->x + b->x;
    ret.y = a->y + b->y;
    return ret;
};

void v2_mults_eq(vec2 *lhs, FLOAT_T scalar) {
    lhs->x *= scalar;
    lhs->y *= scalar;
};

void v2_add_eq(vec2 *lhs, vec2 *rhs) {
    lhs->x += rhs->x;
    lhs->y += rhs->y;
};

smat3 sm3_translate(vec2 *offset) {
    smat3 ret;
    memcpy(&ret, sm3_identity, sizeof(ret));

    ret.c2.x = offset->x;
    ret.c2.y = offset->y;
    return ret;
};

smat3 sm3_rotate(FLOAT_T theta) {
    smat3 ret;
    memcpy(&ret, sm3_identity, sizeof(ret));

    ret.c0.x = F_COS(theta);
    ret.c0.y = F_SIN(theta);

    ret.c1.x = -ret.c0.y; // cos(theta + 90deg) = -cos(90 - theta) = -sin(theta)
    ret.c1.y = ret.c0.x; // sin(theta + 90deg) = sin(90deg - theta) = cos(theta)

    return ret;
};

smat3 sm3_scale(vec2 *scale) {
    smat3 ret;
    memcpy(&ret, sm3_identity, sizeof(ret));
    ret.c0.x = scale->x;
    ret.c1.y = scale->y;

    return ret;
};

smat3 sm3_transform(vec2 *translation, vec2 *scale, FLOAT_T theta) {
    smat3 ret;
    FLOAT_T sin = F_SIN(theta);
    FLOAT_T cos = F_COS(theta);
    ret.c0.x = cos * scale->x;
    ret.c0.y = sin * scale->x;
    ret.c0.z = 0;

    ret.c1.x = -sin * scale->y; // cos(theta + 90deg) = -cos(90 - theta) = -sin(theta)
    ret.c1.y = cos * scale->y; // sin(theta + 90deg) = sin(90deg - theta) = cos(theta)
    ret.c1.z = 0;

    ret.c2.x = translation->x;
    ret.c2.y = translation->y;
    ret.c2.z = 1;

    return ret;
}

FLOAT_T radians(FLOAT_T deg) {
    return deg * (PI / 180);
};

FLOAT_T degrees(FLOAT_T rad) {
    return rad * (180 / PI);
};

void printv3(vec3 *vec) {
    printf("[%f, %f, %f]", vec->x, vec->y, vec->z);
}
void printsm3(smat3 *mat) {
    printf("[\n\t[%f, %f, %f],\n\t[%f, %f, %f],\n\t[%f, %f, %f]\n]",
           mat->c0.x, mat->c1.x, mat->c2.x, mat->c0.y, mat->c1.y, mat->c2.y, mat->c0.z, mat->c1.z, mat->c2.z);
}
void printv2(vec2 *vec) {
    printf("[%f, %f]", vec->x, vec->y);
}




// from GLM https://github.com/g-truc/glm/blob/0.9.5/glm/gtc/matrix_transform.inl
// https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/gluPerspective.xml
smat4 perspective(FLOAT_T fovy, FLOAT_T aspect, FLOAT_T zNear, FLOAT_T zFar) {
    EXIF(aspect == 0, "aspect ratio can't be 0")
    EXIF(zFar == zNear, "Near and far clipping planes must be distinct")

    FLOAT_T const f = 1 / F_TAN(fovy / 2); // cotangent

    smat4 ret;
    memset(&ret, 0, sizeof(ret));

    ret.c0.x = f / aspect;
    ret.c1.y = f;
    ret.c2.z = (zFar + zNear) / (zNear - zFar);
    ret.c2.w = -1;
    ret.c3.z = (2 * zFar * zNear) / ( zNear - zFar);
    return ret;
}

// https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glOrtho.xml
smat4 ortho(FLOAT_T left, FLOAT_T right, FLOAT_T bottom, FLOAT_T top, FLOAT_T zNear, FLOAT_T zFar) {
    smat4 ret;
    memset(&ret, 0, sizeof(ret));

    ret.c0.x = 2 / (right - left);
    ret.c1.y = 2 / (top - bottom);
    ret.c2.z = - 2 / (zFar - zNear);
    ret.c3.x = - (right + left) / (right - left);
    ret.c3.y = - (top + bottom) / (top - bottom);
    ret.c3.z = - (zFar + zNear) / (zFar - zNear);
    ret.c3.w = 1;
    return ret;
}

