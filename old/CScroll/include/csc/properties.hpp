/**
 * @file properties.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-09-26
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CSC_PROPERTIES_HPP
#define CSC_PROPERTIES_HPP

#include <string>

#include "csc/misc_defs.hpp"

namespace csc {
    struct ServerProperties {
        std::string level_name = "world_cscroll";
        std::string motd = "A Minecraft Server (CScroll)";
        std::string text_filtering_config = "";
        std::string resource_pack_sha1 = "";
        std::string level_type = "default"; // ?
        std::string generator_settings = "";
        std::string server_ip = "";
        std::string rcon_password = "";
        std::string resource_pack_prompt = "";
        std::string resource_pack = "";
        int rcon_port = 25575;
        int level_seed = 0;
        int max_build_height = 256;
        int query_port = 25565;
        int network_compression_threshold = 512;
        int max_tick_time = 60000;
        int max_players = 20;
        int server_port = 25565;
        int op_permission_level = 4;
        int entity_broadcast_range_percentage = 100;
        int player_idle_timeout = 0;
        int rate_limit = 0;
        int function_permission_level = 2;
        int spawn_protection = 16;
        int max_world_size = 29999984;
        int view_distance = 4;

        Gamemode gamemode = Gamemode::survival;
        Difficulty difficulty = Difficulty::easy;
        bool enable_jmx_monitoring = false;
        bool enable_command_block = false;
        bool enable_query = false;
        bool pvp = true;
        bool generate_structures = true;
        bool require_resource_pack = false;
        bool use_native_transport = true;
        bool online_mode = true;
        bool enable_status = true;
        bool allow_flight = false;
        bool broadcast_rcon_to_ops = true;
        bool allow_nether = true;
        bool enable_rcon = false;
        bool sync_chunk_writes = true;
        bool prevent_proxy_connections = false;
        bool debug = false;
        bool force_gamemode = false;
        bool hardcore = false;
        bool white_list = false;
        bool broadcast_console_to_ops = false;
        bool spawn_npcs = true;
        bool spawn_animals = true;
        bool snooper_enabled = false;
        bool spawn_monsters = true;
        bool enforce_whitelist = false;
    };

    ServerProperties load_props();
}

#endif