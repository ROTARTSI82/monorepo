//
// Created by Grant on 9/27/19.
//

#pragma once

#ifndef __MASON_IMAGE_H
#define __MASON_IMAGE_H

#include "stb/stb_image.h"

#include "mason/log.h"

#include <string>

namespace mason {
    class image {
    public:
        std::string file_path = "<UNKNOWN PATH>";
        int width = 0, height = 0, channels = 0;

        stbi_uc *data = nullptr;

        explicit image(const std::string &path, bool flip = false);

        ~image();
    };
}


#endif //__MASON_IMAGE_H
