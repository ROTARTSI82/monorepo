//
// Created by grant on 5/29/21.
//

#include "linalg.h"

#include "common.h"

#include <stdio.h>
#include <string.h> // memset

const FLOAT_T sm4_identity[4][4] = {{1, 0, 0, 0},
                                    {0, 1, 0, 0},
                                    {0, 0, 1, 0},
                                    {0, 0, 0, 1}};

// these are extremely inefficient implementations but i'm too lazy to do it properly with assembly so eh

void v4_add_eq(vec4 *lhs, vec4 *rhs) {
    lhs->x += rhs->x;
    lhs->y += rhs->y;
    lhs->z += rhs->z;
    lhs->w += rhs->w;
}

void v4_mults_eq(vec4 *lhs, FLOAT_T scalar) {
    lhs->x *= scalar;
    lhs->y *= scalar;
    lhs->z *= scalar;
    lhs->w *= scalar;
}

vec4 v4_multm(vec4 *a, smat4 *rhs) {
    smat4 crhs = *rhs; // compiler should generate memcpy for me

    vec4 ret;
    memset(&ret, 0, sizeof(ret)); // zero ret

    v4_mults_eq(&crhs.c0.v4, a->x);
    v4_mults_eq(&crhs.c1.v4, a->y);
    v4_mults_eq(&crhs.c2.v4, a->z);
    v4_mults_eq(&crhs.c3.v4, a->w);

    v4_add_eq(&ret, &crhs.c0.v4);
    v4_add_eq(&ret, &crhs.c1.v4);
    v4_add_eq(&ret, &crhs.c2.v4);
    v4_add_eq(&ret, &crhs.c3.v4);

    return ret;
}

vec2 v2_multm(vec2 *a, smat2 *rhs) {
    smat2 crhs = *rhs;

    vec2 ret;
    memset(&ret, 0, sizeof(ret));

    v2_mults_eq(&crhs.c0, a->x);
    v2_mults_eq(&crhs.c1, a->y);

    v2_add_eq(&ret, &crhs.c0);
    v2_add_eq(&ret, &crhs.c1);

    return ret;
}


smat4 sm4_mult(smat4 *lhs, smat4 *rhs) {
    smat4 ret;
    ret.c0.v4 = v4_multm(&lhs->c0.v4, rhs);
    ret.c1.v4 = v4_multm(&lhs->c1.v4, rhs);
    ret.c2.v4 = v4_multm(&lhs->c2.v4, rhs);
    ret.c2.v4 = v4_multm(&lhs->c3.v4, rhs);

    return ret;
}

smat2 sm2_mult(smat2 *lhs, smat2 *rhs) {
    smat2 ret;
    
    ret.c0 = v2_multm(&lhs->c0, rhs);
    ret.c1 = v2_multm(&lhs->c1, rhs);

    return ret;
}


void sm4_mult_eq(smat4 *lhs, smat4 *rhs) {
    v4_multm_eq(&lhs->c0.v4, rhs);
    v4_multm_eq(&lhs->c1.v4, rhs);
    v4_multm_eq(&lhs->c2.v4, rhs);
    v4_multm_eq(&lhs->c3.v4, rhs);
}

void sm2_mult_eq(smat2 *lhs, smat2 *rhs) {
    v2_multm_eq(&lhs->c0, rhs);
    v2_multm_eq(&lhs->c1, rhs);
}

vec2 v2_mults(vec2 *a, FLOAT_T scalar) {
    vec2 ret;
    ret.x = a->x * scalar;
    ret.y = a->y * scalar;
    return ret;
}

FLOAT_T v2_magnitude(vec2 *v) {
    return sqrtf(v->x * v->x + v->y * v->y);
}

vec2 v2_sub(vec2 *a, vec2 *b) {
    vec2 ret;
    ret.x = a->x - b->x;
    ret.y = a->y - b->y;
    return ret;
}


void v2_sub_eq(vec2 *lhs, vec2 *rhs) {
    lhs->x -= rhs->x;
    rhs->y -= rhs->y;
}

vec2 v2_add(vec2 *a, vec2 *b) {
    vec2 ret;
    ret.x = a->x + b->x;
    ret.y = a->y + b->y;
    return ret;
}

FLOAT_T v2_dot(vec2 *a, vec2 *b) {
    return a->x * b->x + a->y * b->y;
}

void v2_mults_eq(vec2 *lhs, FLOAT_T scalar) {
    lhs->x *= scalar;
    lhs->y *= scalar;
}

void v2_add_eq(vec2 *lhs, vec2 *rhs) {
    lhs->x += rhs->x;
    lhs->y += rhs->y;
}

smat4 sm4_translate(vec3 *offset) {
    smat4 ret;
    memcpy(&ret, sm4_identity, sizeof(ret));

    ret.c3.v4.x = offset->x;
    ret.c3.v4.y = offset->y;
    ret.c3.v4.z = offset->z;
    return ret;
}

smat4 sm4_rotate(FLOAT_T theta) {
    smat4 ret;
    memcpy(&ret, sm4_identity, sizeof(ret));

    ret.c0.v2.x = F_COS(theta);
    ret.c0.v2.y = F_SIN(theta);

    ret.c1.v2.x = -ret.c0.v2.y; // cos(theta + 90deg) = -cos(90 - theta) = -sin(theta)
    ret.c1.v2.y = ret.c0.v2.x; // sin(theta + 90deg) = sin(90deg - theta) = cos(theta)

    return ret;
}

smat2 sm2_rotate(FLOAT_T theta) {
    smat2 ret;
    ret.c0.x = F_COS(theta);
    ret.c0.y = F_SIN(theta);

    ret.c1.x = -ret.c0.y;
    ret.c1.y = ret.c0.x;

    return ret;
}


smat4 sm4_scale(vec2 *scale) {
    smat4 ret;
    memcpy(&ret, sm4_identity, sizeof(ret));
    ret.c0.v2.x = scale->x;
    ret.c1.v2.y = scale->y;

    return ret;
}

smat2 sm2_scale(vec2 *scale) {
    smat2 ret;
    ret.c0.x = scale->x;
    ret.c1.y = scale->y;

    ret.c0.y = 0;
    ret.c1.x = 0;

    return ret; 
}

smat4 sm4_transform(vec3 *translation, vec2 *scale, FLOAT_T theta) {
    smat4 ret;
    memcpy(&ret, sm4_identity, sizeof(ret));

    FLOAT_T sin = F_SIN(theta);
    FLOAT_T cos = F_COS(theta);
    ret.c0.v4.x = cos * scale->x;
    ret.c0.v4.y = sin * scale->x;

    ret.c1.v4.x = -sin * scale->y; // cos(theta + 90deg) = -cos(90 - theta) = -sin(theta)
    ret.c1.v4.y = cos * scale->y; // sin(theta + 90deg) = sin(90deg - theta) = cos(theta)

    ret.c3.v4.x = translation->x;
    ret.c3.v4.y = translation->y;
    ret.c3.v4.z = translation->z;
    return ret;
}

smat2 sm2_transform(vec2 *scale, FLOAT_T theta) {
    smat2 ret;
    FLOAT_T sin = F_SIN(theta);
    FLOAT_T cos = F_COS(theta);

    ret.c0.x = cos * scale->x;
    ret.c0.y = sin * scale->x;

    ret.c1.x = -sin * scale->y;
    ret.c1.y = cos * scale->y;

    return ret;
}

FLOAT_T radians(FLOAT_T deg) {
    return deg * (PI / 180);
}

FLOAT_T degrees(FLOAT_T rad) {
    return rad * (180 / PI);
}

void printv3(vec3 *vec) {
    printf("[%f, %f, %f]", vec->x, vec->y, vec->z);
}
void printsm4(smat4 *mat) {
    printf("[\n\t[%f, %f, %f, %f],\n\t[%f, %f, %f, %f],\n\t[%f, %f, %f, %f],\n\t[%f, %f, %f, %f]\n]",
           mat->c0.v4.x, mat->c1.v4.x, mat->c2.v4.x, mat->c3.v4.x,
           mat->c0.v4.y, mat->c1.v4.y, mat->c2.v4.y, mat->c3.v4.y,
           mat->c0.v4.z, mat->c1.v4.z, mat->c2.v4.z, mat->c3.v4.z,
           mat->c0.v4.w, mat->c1.v4.w, mat->c2.v4.w, mat->c3.v4.w);
}
void printsm2(smat2 *mat) {
    printf("[\n\t[%f, %f],\n\t[%f, %f]\n]", mat->c0.x, mat->c1.x, mat->c0.y, mat->c1.y);
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

    ret.c0.v4.x = f / aspect;
    ret.c1.v4.y = f;
    ret.c2.v4.z = (zFar + zNear) / (zNear - zFar);
    ret.c2.v4.w = -1;
    ret.c3.v4.z = (2 * zFar * zNear) / ( zNear - zFar);
    return ret;
}

// https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glOrtho.xml
smat4 ortho(FLOAT_T left, FLOAT_T right, FLOAT_T bottom, FLOAT_T top, FLOAT_T zNear, FLOAT_T zFar) {
    smat4 ret;
    memset(&ret, 0, sizeof(ret));

    ret.c0.v4.x = 2 / (right - left);
    ret.c1.v4.y = 2 / (top - bottom);
    ret.c2.v4.z = - 2 / (zFar - zNear);
    ret.c3.v4.x = - (right + left) / (right - left);
    ret.c3.v4.y = - (top + bottom) / (top - bottom);
    ret.c3.v4.z = - (zFar + zNear) / (zFar - zNear);
    ret.c3.v4.w = 1;
    return ret;
}

