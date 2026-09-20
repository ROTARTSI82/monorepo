//
// Created by Grant on 2019-09-01.
//

#ifndef MASON_IMGUI_INCLUDES_H
#define MASON_IMGUI_INCLUDES_H

#include "imgui/imgui.cpp"
#include "imgui/impl/imgui_impl_glfw.cpp"

#undef IMGUI_IMPL_OPENGL_LOADER_GL3W
#undef IMGUI_IMPL_OPENGL_LOADER_GLAD
#define IMGUI_IMPL_OPENGL_LOADER_GLEW

#include "imgui/impl/imgui_impl_opengl3.cpp"
#include "imgui/imgui_draw.cpp"
#include "imgui/imgui_widgets.cpp"
#include "imgui/imgui_demo.cpp"

#endif //MASON_IMGUI_INCLUDES_H
