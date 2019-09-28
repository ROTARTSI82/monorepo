//
// Created by Grant on 9/27/19.
//


#include "mason/image.h"

namespace mason {
    image::image(const std::string &path, bool flip) : file_path(path) {
        stbi_set_flip_vertically_on_load(flip);

        data = stbi_load(path.c_str(), &width, &height, &channels, 4);

        if (!data) {
            MASON_WARN("Failed to load image at {}", path);
        }
    }

    image::~image() {
        stbi_image_free(data);
    }
}
