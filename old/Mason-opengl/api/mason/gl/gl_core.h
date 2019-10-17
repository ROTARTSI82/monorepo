//
// Created by Grant on 9/26/19.
//
#pragma once

#ifndef MASONSHARE_GL_CORE_H
#define MASONSHARE_GL_CORE_H

#include "mason/log.h"

#include "GL/glew.h"
#include "GLFW/glfw3.h"

#ifdef MASON_DEBUG_MODE
#define IMGUI_IMPL_OPENGL_LOADER_GLEW

#include "imgui/imgui.h"
#include "imgui/imgui_impl_opengl2.h"
#include "imgui/imgui_impl_glfw.h"

#endif

namespace mason::gl {
    void init_glew();

    void init_glfw();

    void init_gl();

    void handle_single_error(const std::string &msg = "Unknown Error");

    void flush_errors(const std::string &msg = "Unknown Error");

    extern void (*quit_glfw)();

    void quit_imgui();
}

#endif //MASONSHARE_GL_CORE_H
