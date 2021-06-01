//
// Created by grant on 5/29/21.
//

#pragma once

#ifndef GAMETEST_LINALG_H
#define GAMETEST_LINALG_H

#include <math.h>

// assuming aspect is width / height
// fullFovX = 2 * atan(tan(fullFovY / 2) * aspect)
// fullFovY = atan(tan(fullFovX / 2) / aspect) * 2

// halfFovX = atan(aspect * tan(halfFovY))
// halfFovY = atan(tan(halfFovX) / aspect)


// in case we ever want to switch to double or long double
#define FLOAT_T float
#define F_COS(x) cosf(x)
#define F_ACOS(x) acosf(x)

#define F_SIN(x) sinf(x)
#define F_ASIN(x) asinf(x)
#define F_TAN(x) tanf(x)
#define GL_FLOAT_T GL_FLOAT

#define PI 0x1.921fb6p+1f
/*
 * float = 0x1.921fb6p+1f
 * double = 0x1.921fb54442d18p+1
 * long double = 0xc.90fdaa22168c235p-2L
 */

// something something strict aliasing

typedef struct {
    FLOAT_T x;
    FLOAT_T y;
} vec2;


typedef struct {
    FLOAT_T x;
    FLOAT_T y;
    FLOAT_T z;
} vec3;


typedef struct {
    FLOAT_T x;
    FLOAT_T y;
    FLOAT_T z;
    FLOAT_T w; // should this be first?
} vec4;

extern const FLOAT_T sm4_identity[4][4];


typedef struct {
    vec4 c0; // c stands for column
    vec4 c1;
    vec4 c2;
    vec4 c3;
} smat4; // Square MATrix 4x4



void v4_add_eq(vec4 *lhs, vec4 *rhs);

void v4_mults_eq(vec4 *lhs, FLOAT_T scalar);

vec4 v4_multm(vec4 *a, smat4 *rhs); // vec2 multiply matrix

// todo: why mark extern
static inline void v4_multm_eq(vec4 *lhs, smat4 *rhs) {
    *lhs = v4_multm(lhs, rhs);
}



// transform lhs by rhs
smat4 sm4_mult(smat4 *lhs, smat4 *rhs);

void sm4_mult_eq(smat4 *lhs, smat4 *rhs);


vec2 v2_add(vec2 *a, vec2 *b);

FLOAT_T v2_dot(vec2 *a, vec2 *b);

vec2 v2_mults(vec2 *a, FLOAT_T scalar); // vec2 multiply scalar

//FLOAT_T v2_magnitude(vec2 *v);

vec2 v2_sub(vec2 *a, vec2 *b);

void v2_sub_eq(vec2 *lhs, vec2 *rhs);

//void v2_mults_eq(vec2 *lhs, FLOAT_T scalar);

void v2_add_eq(vec2 *lhs, vec2 *rhs);


smat4 sm4_translate(vec3 *offset);
smat4 sm4_rotate(FLOAT_T theta); // counterclockwise rotation in radians
smat4 sm4_scale(vec2 *scale);

// shortcut for applying all transformations. Applied in the order of: Scaling/Rotation, then Translation.
smat4 sm4_transform(vec3 *translation, vec2 *scale, FLOAT_T theta);

//FLOAT_T radians(FLOAT_T deg);
//FLOAT_T degrees(FLOAT_T rad);


// these do not print newlines.
//void printv3(vec3 *vec);
//void printsm4(smat4 *mat);
//void printv2(vec2 *vec);

// from GLM https://github.com/g-truc/glm/blob/0.9.5/glm/gtc/matrix_transform.inl
smat4 perspective(FLOAT_T fovy, FLOAT_T aspect, FLOAT_T zNear, FLOAT_T zFar);
smat4 ortho(FLOAT_T left, FLOAT_T right, FLOAT_T bottom, FLOAT_T top, FLOAT_T near, FLOAT_T far);


#endif //GAMETEST_LINALG_H
