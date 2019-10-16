//
// Created by Grant on 9/26/19.
//
#pragma once

#ifndef MASONSHARE_GL_CORE_H
#define MASONSHARE_GL_CORE_H

#include "mason/log.h"

#include "GL/glew.h"
#include "GLFW/glfw3.h"

namespace mason::gl {
    void init_glew();

    void init_glfw();

    void init_gl();

    void handle_single_error(const std::string &msg = "Unknown Error");

    void flush_errors(const std::string &msg = "Unknown Error");

    extern void (*quit)();
}

#endif //MASONSHARE_GL_CORE_H
