//
// Created by 25granty on 10/13/19.
//

#include "mason/al/al_source.h"

namespace mason::al {

    al_source::al_source() {
        alGenSources(1, &src);
    }

    al_source::~al_source() {
        alDeleteSources(1, &src);
    }

    void al_source::play() {
        alSourcePlay(src);
    }

    void al_source::bind_buffer(mason::al::al_buffer *buf) {
        alSourcei(src, AL_BUFFER, buf->buf);
    }

    void al_source::set_pitch(float pitch) {
        alSourcef(src, AL_PITCH, pitch);
    }

    void al_source::set_gain(float gain) {
        alSourcef(src, AL_GAIN, gain);
    }

    void al_source::set_position(glm::vec3 pos) {
        alSource3f(src, AL_POSITION, pos.x, pos.y, pos.z);
    }

    void al_source::set_velocity(glm::vec3 vel) {
        alSource3f(src, AL_VELOCITY, vel.x, vel.y, vel.z);
    }

    void al_source::set_looping(bool looping) {
        alSourcei(src, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    void al_source::stop() {
        alSourceStop(src);
    }

    void al_source::pause() {
        alSourcePause(src);
    }

    bool al_source::is_playing() {
        ALenum source_state;
        alGetSourcei(src, AL_SOURCE_STATE, &source_state);
        return source_state == AL_PLAYING;
    }
}