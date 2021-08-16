#include "cel/world.hpp"

#include "cel/app.hpp"

namespace cel {
    world_t::world_t(::cel::app_t *a) : parent(a) {}

    world_t::~world_t() {

    }
}