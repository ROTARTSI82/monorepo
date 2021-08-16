/**
 * @file world.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-07-21
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_WORLD_HPP
#define CEL_WORLD_HPP

#include <vector>
#include <typeindex>
#include <unordered_map>
#include <memory>

#include "cel/eng/input.hpp"
#include "cel/game/player.hpp"


namespace cel {
    struct draw_instance;
    class game_obj {
    public:
        virtual ~game_obj() = default;

        virtual void tick() = 0;
        virtual draw_instance *enqueue_render(draw_instance *insert_point) = 0; 
    };

    class layer {
    private:
    public:
        // expected constructor: layer(world &)

        virtual ~layer() = default;

        virtual void tick(const input_frame &input) = 0; // tick entities
        virtual void upload() = 0; // upload instance vbos to the gpu
        virtual void draw() = 0; // dispatch draw calls
    };

    class app_t;
    class world_t {
    public:
        app_t *parent;
        std::vector<std::shared_ptr<layer>> layers;
        std::unordered_map<std::type_index, std::shared_ptr<layer>> layer_cache;
        player_t player;

        world_t(::cel::app_t *);
        ~world_t();


        inline void tick(const input_frame &inp) {
            for (const auto &layer : layers) {
                layer->tick(inp);
        }
        };

        inline void upload() {
            for (const auto &layer : layers)
                layer->upload(); // glBufferSubData()
        };

        inline void draw() {
            for (const auto &layer : layers)
                layer->draw(); // glDrawArraysInstanced()
        };

        template <typename T>
        std::shared_ptr<layer> get_layer() {
            const std::type_index ind = std::type_index(typeid(T));
            if (layer_cache.count(ind)) return layer_cache.at(ind);
            const std::shared_ptr<layer> nlayer_ptr = std::dynamic_pointer_cast<layer>(std::make_shared<T>(*this));
            layer_cache[ind] = nlayer_ptr;
            return nlayer_ptr;
        }

        inline void unload_layer(std::type_index id) { layer_cache.erase(id); }
        inline void clear_layer_cache() { layer_cache.clear(); }

        // TODO: Save states-- serialize & deserialize/load. With zlib compression.
    };
}

#endif