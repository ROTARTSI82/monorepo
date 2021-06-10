//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef GAMETEST_CONFIG_H
#define GAMETEST_CONFIG_H

// declarations found in common.c
extern float GFB_WIDTH;
extern float GFB_HEIGHT;

#define GFOV 90
#define ZNEAR 0.1f
#define ZFAR 100

#define MAX_BLITS 16

// 16 mb
#define MAX_IMG_SIZE 16777216
#define MAX_FILE_SIZE 16777216

// A collision will register when the player is closer than this value
// to an object. The player won't be pushed out this far so a collision would
// continually be registered when touching or standing on geometry.
#define COLLIDE_THRESHOLD 0.1

#define GCLOCK_SRC CLOCK_MONOTONIC
#define GPHYS_FPS 60

#define M_STRIFY(name) #name

#define APP_VERSION "develpment version " __DATE__ " " __TIME__" "__VERSION__

#endif //GAMETEST_CONFIG_H
