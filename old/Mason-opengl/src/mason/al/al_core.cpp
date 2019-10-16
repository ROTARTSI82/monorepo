//
// Created by 25granty on 10/13/19.
//

#include "mason/al/al_core.h"

namespace mason::al {

#ifdef MASON_DEBUG_MODE

    void handle_single_error(const std::string &msg) {
        ALenum err = alGetError();
        if (err != AL_NO_ERROR) {
            MASON_WARN("[** OPENAL ERROR: {} **]: {}", err, msg);
        }
    }

    void flush_errors(const std::string &msg) {
        ALenum err = alGetError();
        while (err != AL_NO_ERROR) {
            MASON_WARN("[** OPENAL ERROR: {} **]: {}", err, msg);
            err = alGetError();
        }
    }

#else
    void flush_errors(const std::string &msg) {}
    void handle_single_error(const std::string &msg) {}
#endif
}
