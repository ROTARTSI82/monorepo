//
// Created by Grant on 9/26/19.
//

#include "mason/mason.h"

namespace mason {
#ifdef MASON_DEBUG_MODE
#pragma message("Mason debug mode is ACTIVE! Disable this in release mode using #undef MASON_DEBUG_MODE before including mason!")
    bool debug_mode = true;
#else
    bool debug_mode = false;
#endif
}
