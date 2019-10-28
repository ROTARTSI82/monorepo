//
// Created by Grant on 9/26/19.
//
#pragma once

#ifndef __MASON_GL_CORE_H
#define __MASON_GL_CORE_H

#include "mason/log.h"

#include "GL/glew.h"
#include "GLFW/glfw3.h"

#ifdef MASON_ENABLE_IMGUI

#define IMGUI_IMPL_OPENGL_LOADER_GLEW

#include "imgui/imgui.h"
#include "imgui/imgui_impl_opengl3.h"
#include "imgui/imgui_impl_glfw.h"

#endif

namespace mason::gl {
    void init_glfw();

    void init_glew(bool use_expiremental = true);

    void handle_single_error(const std::string &msg = "Unknown Error");

    void flush_errors(const std::string &msg = "Unknown Error");

    extern void (*quit)();

    void quit_imgui();
}

#endif //__MASON_GL_CORE_H
