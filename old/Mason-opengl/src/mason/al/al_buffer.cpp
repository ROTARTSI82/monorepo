//
// Created by 25granty on 10/14/19.
//

#include "mason/al/al_buffer.h"
#include "stb/stb_vorbis.c"  // Has to be included in the c++ source file or else we die from linker errors.

namespace mason::al {

    // TODO: Add proper error checking here (Hopefully I'll get unlazy later)
    al_buffer::al_buffer(const std::string &path) {
        int channels, sample_rate;
        short *data;
        int data_size = stb_vorbis_decode_filename(path.c_str(), &channels, &sample_rate, &data);

        ALenum fmt;

        switch (sample_rate) {
            case (16): {
                fmt = channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
                break;
            }
            case (8): {
                fmt = channels > 1 ? AL_FORMAT_STEREO8 : AL_FORMAT_MONO8;
                break;
            }

            default:
                fmt = AL_FORMAT_MONO16;
        }

        if (fmt == -1) {
            MASON_WARN("Failed to load {}: Invalid format of {} channels, {} sample rate", path, channels, sample_rate);
        } else if (data == nullptr || !data_size || data_size == -1) {
            MASON_WARN("Failed to load {}: Data has size 0 or is nullptr", path);
        }

        alGenBuffers(1, &buf);

        alBufferData(buf, fmt, (const void *) data, data_size, sample_rate);
    }

    al_buffer::~al_buffer() {
        alDeleteBuffers(1, &buf);
    }
};

