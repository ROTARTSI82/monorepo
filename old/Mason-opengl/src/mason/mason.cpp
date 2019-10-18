//
// Created by Grant on 9/26/19.
//

#include "mason/mason.h"

namespace mason {
#ifdef MASON_ENABLE_IMGUI
    bool imgui_enabled = true;
#else
    bool imgui_enabled = false;
#endif

#ifdef MASON_ENABLE_LOGGING
    bool logging_enabled = true;
#else
    bool logging_enabled = false;
#endif

}
