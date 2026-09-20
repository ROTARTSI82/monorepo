#include "cel/eng/audio.hpp"

#include "cel/eng/log.hpp"
#include "stb/stb_vorbis.c"

namespace cel {
    std::vector<const ALCchar *> enumerate_speakers() {
        std::vector<const ALCchar *> ret;
        const ALCchar *devices;

        if (alcIsExtensionPresent(nullptr, "ALC_ENUMERATE_ALL_EXT") == AL_TRUE) {
            devices = alcGetString(nullptr, ALC_ALL_DEVICES_SPECIFIER);
        } else if (alcIsExtensionPresent(nullptr, "ALC_ENUMERATION_EXT") == AL_TRUE) {
            devices = alcGetString(nullptr, ALC_DEVICE_SPECIFIER);
        } else {
            throw std::runtime_error{"Cannot enumerate OpenAL devices: Both ALC_ENUMERATION_EXT and ALC_ENUMERATE_ALL_EXT are missing"};
        }

        while (!(*devices == '\0' && *(devices + 1) == '\0')) {
            ret.emplace_back(devices);
            devices += std::strlen(devices) + 1;
        }

        return ret;
    }

    const ALCchar *get_default_speaker() {
        if (alcIsExtensionPresent(nullptr, "ALC_ENUMERATE_ALL_EXT") == AL_TRUE) {
            return alcGetString(nullptr, ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
        } else if (alcIsExtensionPresent(nullptr, "ALC_ENUMERATION_EXT") == AL_TRUE) {
            return alcGetString(nullptr, ALC_DEFAULT_DEVICE_SPECIFIER);
        }

        throw std::runtime_error("Cannot get default OpenAL device: Both ALC_ENUMERATION_EXT and ALC_ENUMERATE_ALL_EXT are missing");
    }

    void flush_al_error(const std::string &pos) {
        CEL_INFO("flush_al_error @ {}", pos);
        ALCenum error = alGetError();
        while (error != AL_NO_ERROR) {
            CEL_WARN("AL_ERROR: {}", error);
            ALCenum nerror = alGetError();
            error = error != nerror ? nerror : AL_NO_ERROR;
        }
    }


    inline tracked_al_buf::tracked_al_buf() {
        alGenBuffers(1, &id);
    }

    inline tracked_al_buf::~tracked_al_buf() {
        stop_for_reload();
    }

    inline void tracked_al_buf::stop_for_reload() const {
        alDeleteBuffers(1, &id);
    }

    void tracked_al_buf::reload() {
        alGenSources(1, &id);
        // on_reload(*this);
    }

    al_file_buf::al_file_buf(audio_engine *eng, const std::string &file, bool essential) : parent(eng), filename(file), essential(essential), tracked_al_buf() {
        load();
    }

    void al_file_buf::reload() {
        alGenBuffers(1, &id);
        load();
    }

    void al_file_buf::load() {
        short *data = nullptr;
        int channels = 0;
        int sampleRate = 0;

        std::string actual_path = parent->opt->res_path(filename, essential);
        int len = stb_vorbis_decode_filename(actual_path.c_str(), &channels, &sampleRate, &data);
        if (len == 0 || channels == 0 || sampleRate == 0 || data == nullptr) {
            CEL_ERROR("Failed to load audio from {} = res_path({}, {})", actual_path, filename, essential);
            if (data) free(data);
            if (essential) throw std::runtime_error{"Failed to load essential audio"};

            // generate audio here
            CEL_CRITICAL("Placeholder SFX generation is not implemented yet.");
        }

        if (channels > 1) {
            len *= 2 * sizeof(short);
            // CEL_TRACE("alBufferData({}, AL_FORMAT_STEREO16, {}, {}, {})", id, (uint64_t) data, len, sampleRate);
            alBufferData(id, AL_FORMAT_STEREO16, data, len, sampleRate);
        } else {
            len *= sizeof(short);
            CEL_TRACE("MONO");
            alBufferData(id, AL_FORMAT_MONO16, data, len, sampleRate);
        }

        free(data); // !VERY IMPORTANT!
    }

    void al_file_buf::dec_ref() {
        if (--refs <= 0) {
            parent->file_bufs.erase(this);
            delete this;
        }
    }


    inline tracked_source::tracked_source() {
        alGenSources(1, &id);
    }

    inline tracked_source::~tracked_source() {
        stop_for_reload();
    }

    void tracked_source::stop_for_reload() {
        alSourceStop(id);
        alDeleteSources(1, &id);
    }

    void tracked_source::reload(float gain) {
        alGenSources(1, &id);
        set_gain(gain);
    }

    [[nodiscard]] inline ALint tracked_source::get_num_processed() const {
        ALint ret = 0;
        alGetSourcei(id, AL_BUFFERS_PROCESSED, &ret);
        return ret;
    }

    [[nodiscard]] inline bool tracked_source::is_playing() const {
        ALint state;
        alGetSourcei(id, AL_SOURCE_STATE, &state);
        return state == AL_PLAYING;
    }

    void tracked_source::set_ori(const vec3 &forward, const vec3 &up) const {
        float ori[6] = {forward.x, forward.y, forward.z, up.x, up.y, up.z};
        alSourcefv(id, AL_ORIENTATION, ori);
    }


    source_pool::source_pool(int n) {
        CEL_TRACE("source_pool ctor");
        for (int i = 0; i < n; i++)
            add_source();
    }

    source_pool::~source_pool() {
        for (const auto &i : used)
            delete i;
        for (const auto &i : free)
            delete i;
    }

    void source_pool::cycle_gc() {
        for (auto it = used.begin(); it != used.end();) {
            if ((*it)->can_be_freed()) {
                CEL_WARN("Freeing source");
                free.emplace_back(*it);
                it = used.erase(it);
            } else ++it;
        }
    }

    source_ref source_pool::borrow_one() {
        if (free.empty()) {
            cycle_gc();
            if (free.empty()) {
                CEL_WARN("Source pool miss! {} misses so far.", ++num_misses);
                return *(used.emplace(new tracked_source{}).first); // do this only under certain conditions?

                return source_ref{nullptr};
            }
        }

        tracked_source *ret = free.back();
        free.pop_back();
        used.emplace(ret);
        return source_ref{ret};
    }

    void source_pool::stop_for_reload() {
        for (const auto &i : free)
            i->stop_for_reload();
        for (const auto &i : used)
            i->stop_for_reload();
    }

    void source_pool::set_gain(float g) {
        gain = g;
        for (const auto &i : free)
            i->set_gain(gain);
        for (const auto &i : used)
            i->set_gain(gain);
    }

    void source_pool::reload() {
        for (const auto &i : free)
            i->reload(gain);
        for (const auto &i : used)
            i->reload(gain);
    }


    impl_al_dev_and_ctx_initializer::impl_al_dev_and_ctx_initializer() {
        CEL_INFO("Initializing OpenAL");
        dev = alcOpenDevice(nullptr);
        ctx = alcCreateContext(dev, nullptr);
        if (!alcMakeContextCurrent(ctx)) {
            throw std::runtime_error{"Failed to make OpenAL context current!"};
        }

        // ALfloat listenerOri[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f };
        // alListener3f(AL_POSITION, 0, 0, 1.0f);
        // alListener3f(AL_VELOCITY, 0, 0, 0);
        // alListenerfv(AL_ORIENTATION, listenerOri);
    }

    audio_engine::audio_engine(settings_handler *o) : impl_al_dev_and_ctx_initializer(), opt(o), music(o->music_pool_size) {}

    audio_engine::~audio_engine() {
        for (auto i : file_bufs) {
            delete i;
        }

        alcMakeContextCurrent(nullptr);
        alcDestroyContext(ctx);
        alcCloseDevice(dev);
    }

    void audio_engine::cycle_gc() {
        for (auto it = file_bufs.begin(); it != file_bufs.end(); ) {
            if ((*it)->get_num_refs() <= 0) {
                CEL_WARN("File buf erased");
                it = file_bufs.erase(it);
            } else ++it;
        }

        music.cycle_gc();
    }

    sound_ref audio_engine::sound_from_file(const std::string &filename, bool essential) {
        return sound_ref{*(file_bufs.emplace(new al_file_buf{this, filename, essential}).first)};
    }

    void audio_engine::set_output_device(const ALCchar *dev_name) {
        for (auto i : file_bufs) {
            i->stop_for_reload();
        }

        music.stop_for_reload();

        alcMakeContextCurrent(nullptr);
        alcDestroyContext(ctx);
        alcCloseDevice(dev);

        dev = alcOpenDevice(dev_name);
        ctx = alcCreateContext(dev, nullptr);
        if (!alcMakeContextCurrent(ctx)) {
            throw std::runtime_error{"Failed to make OpenAL context current!"};
        }

        for (auto i : file_bufs) {
            i->reload();
        }

        music.reload();

        // ALfloat listenerOri[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f };
        // alListener3f(AL_POSITION, 0, 0, 1.0f);
        // alListener3f(AL_VELOCITY, 0, 0, 0);
        // alListenerfv(AL_ORIENTATION, listenerOri);
    }

    void audio_engine::reload_file_bufs() {
        for (auto i : file_bufs) {
            i->load();
        }
    }
}
