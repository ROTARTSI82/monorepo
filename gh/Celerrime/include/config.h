//
// Created by grant on 5/28/21.
//

#pragma once

#ifndef GAMETEST_CONFIG_H
#define GAMETEST_CONFIG_H

#define GWIN_WIDTH (16*128)
#define GWIN_HEIGHT (9*128)

#define GUPDATE_PROJECTION 1

// #define GFOV 22.89616613
#define GFOV 49.90734289
//#define GFOV 180
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


#endif //GAMETEST_CONFIG_H
