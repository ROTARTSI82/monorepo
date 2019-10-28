//
// Created by 25granty on 10/13/19.
//

#include <mason/al/al_context.h>
#include "mason/al/al_core.h"

namespace mason::al {

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

    mason::al::al_context *init_al_default() {
        mason::al::update_default_device();
        auto cont = new mason::al::al_context(mason::al::default_device->dev_obj);
        cont->bind();
        return cont;
    }

}
