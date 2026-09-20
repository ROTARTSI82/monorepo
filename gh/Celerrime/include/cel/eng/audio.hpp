/**
 * @file audio.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-09-05
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_AUDIO_HPP
#define CEL_ENG_AUDIO_HPP

#include <AL/al.h>
#include <AL/alc.h>
#include <functional>
#include <unordered_set>
#include <vector>

#include "cel/eng/linalg.hpp"
#include "cel/settings.hpp"
#include "cel/eng/misc_util.hpp"


namespace cel {
    std::vector<const ALCchar *> enumerate_speakers();
    const ALCchar *get_default_speaker();

    void flush_al_error(const std::string &pos);

    class tracked_al_buf {
    protected:
        int refs = 0;

    public:
        ALuint id;

        [[nodiscard]] inline int get_num_refs() const noexcept { return refs; }

        // std::function<void(tracked_al_buf &)> on_reload = [](tracked_al_buf &hic) {

        // };

        tracked_al_buf();
        virtual ~tracked_al_buf();

        void stop_for_reload() const;
        void reload();

        CEL_DEL_CPY_OP_CTOR(tracked_al_buf);

        inline void inc_ref() { refs++; }
        inline void dec_ref() { std::cout << "tracked_al_buf::dec_ref()" << std::endl; refs--; }
    };

    class audio_engine;
    class al_file_buf : public tracked_al_buf {
    private:
        audio_engine *parent;
        std::string filename;
        bool essential;

        void load();
        friend class audio_engine;

    public:
        al_file_buf() = delete;
        al_file_buf(audio_engine *eng, const std::string &file, bool essential);
        ~al_file_buf() override = default;

        // stop_for_reload inherited
        void reload();

        void dec_ref();

        inline bool operator==(const al_file_buf &rhs) const noexcept { return id == rhs.id; }
    };

    // typedef counting_ref<tracked_al_buf> al_buf_ref;
    typedef counting_ref<al_file_buf> sound_ref;

    class source_pool;
    class tracked_source {
    private:
        int refs = 0;

    public:
        ALuint id;

        tracked_source();
        ~tracked_source();

        void stop_for_reload();
        void reload(float gain);

        CEL_DEL_CPY_OP_CTOR(tracked_source);

        ALint dequeue_all() const;

        [[nodiscard]] ALint get_num_processed() const;

        [[nodiscard]] bool is_playing() const;

        void set_ori(const vec3 &forward, const vec3 &up) const;

        inline bool can_be_freed() { return refs <= 0 && !is_playing(); }
        inline void enqueue_buf(const tracked_al_buf *buf) const { std::cout << "enqueue " << buf->id << std::endl; alSourceQueueBuffers(id, 1, &buf->id); }
        inline void stop() const { std::cout << "SOURCE STOP" << std::endl; alSourceStop(id); }
        inline void pause() const { alSourcePause(id); }
        inline void rewind() const { alSourceRewind(id); }
        inline void play() const { alSourcePlay(id); }
        inline void dequeue_bufs(ALsizei num, ALuint *bufsRemoved) const { alSourceUnqueueBuffers(id, num, bufsRemoved); }
        inline void set_looping(bool looping) const { alSourcei(id, AL_LOOPING, looping ? AL_TRUE : AL_FALSE); }
        inline void set_pitch(float pitch) const { alSourcef(id, AL_PITCH, pitch); }
        inline void set_gain(float gain) const { alSourcef(id, AL_GAIN, gain); }
        inline void set_vel(const vec3 &vel) const { alSource3f(id, AL_VELOCITY, vel.x, vel.y, vel.z); }
        inline void set_pos(const vec3 &pos) const { alSource3f(id, AL_POSITION, pos.x, pos.y, pos.z); }
        inline void bind_buf(const tracked_al_buf *buf) const { alSourcei(id, AL_BUFFER, buf->id); }

        inline void inc_ref() { refs++; }
        inline void dec_ref() { if (refs > 0) refs--; }
    };

    typedef counting_ref<tracked_source> source_ref;

    class source_pool {
    private:
        uint32_t num_misses = 0;
        float gain = 1.0f;

    public:
        std::vector<tracked_source *> free;
        std::unordered_set<tracked_source *> used;

        source_pool() = delete;
        source_pool(int num_sources);
        ~source_pool();

        void cycle_gc(); // auto triggered when free list is empty and borrow_one() is called
        source_ref borrow_one(); /// returns a null reference if we run out of pools.

        // functions to add/remove sources
        inline void add_source() {
            free.emplace_back(new tracked_source{});
        }

        void set_gain(float gain);

        void stop_for_reload();
        void reload();

        inline void remove_source() {
            delete free.back();
            free.pop_back();
        }

        inline std::size_t get_num_sources() {
            return free.size() + used.size();
        }
    };

    class impl_al_dev_and_ctx_initializer {
    protected:
        ALCdevice *dev;
        ALCcontext *ctx;

        impl_al_dev_and_ctx_initializer();
    };

    class audio_engine : public impl_al_dev_and_ctx_initializer {
    public:
        // std::unordered_set<tracked_al_buf> bufs;
        std::unordered_set<al_file_buf *> file_bufs;

        settings_handler *opt;
        source_pool music;

        audio_engine() = delete;
        audio_engine(settings_handler *);
        ~audio_engine();

        void cycle_gc();
        sound_ref sound_from_file(const std::string &filename, bool essential = false);
        void reload_file_bufs(); // for if the options filepaths change. NOTE: This does NOT recreate the buffer. It merely calls alBufferData()

        void set_output_device(const ALCchar *dev);
    };
}

#endif