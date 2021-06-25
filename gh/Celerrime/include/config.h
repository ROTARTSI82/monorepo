//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef CEL_CONFIG_H
#define CEL_CONFIG_H

// declarations found in common.c
extern float CEL_FB_WIDTH;
extern float CEL_FB_HEIGHT;

#define GOPENGL_DEBUG_CONTEXT 1

#define CEL_FOV 90
#define CEL_ZNEAR 0.1f
#define CEL_ZFAR 100

#define CEL_MAX_BLITS 16

// 16 mb
#define CEL_MAX_IMG_SIZE 16777216
#define CEL_MAX_FILE_SIZE 16777216

// A collision will register when the player is closer than this value
// to an object. The player won't be pushed out this far so a collision would
// continually be registered when touching or standing on geometry.
#define CEL_COLLIDE_THRESHOLD 0.1

#define CEL_CLOCK_SRC CLOCK_MONOTONIC
#define CEL_PHYS_FPS 60

#define M_STRIFY(name) #name

#define CEL_VERSION "Celerrime development version"
#define CEL_VERSION_VERBOSE CEL_VERSION " " __DATE__ " " __TIME__" "__VERSION__

#endif //CEL_CONFIG_H
