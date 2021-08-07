#include "cel/constants.hpp"

namespace cel {
    template<>
    GLenum consts_for<float>::gl_type_enum = GL_FLOAT;
}