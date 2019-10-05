//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef MASONSHARE_SHADER_PROGRAM_H
#define MASONSHARE_SHADER_PROGRAM_H

#include "mason/gl/gl_core.h"

#include <unordered_map>
#include <fstream>
#include <algorithm>

namespace mason::gl {

    GLuint compile_shader(const std::string &type, const std::string &src, const std::string &full_path);

    class shader_program {
    public:
        std::unordered_map<std::string, GLint> uniform_cache;

        GLuint id = 0;

        explicit shader_program(const std::string &path);

        ~shader_program();

        GLint get_uniform_location(const std::string &name);

        void bind();
    };

}


#endif //MASONSHARE_SHADER_PROGRAM_H
