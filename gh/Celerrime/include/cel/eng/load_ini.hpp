/**
 * @file load_ini.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-08-13
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CEL_ENG_LOAD_INI_HPP
#define CEL_ENG_LOAD_INI_HPP

#include <unordered_map>
#include <string>

namespace cel {
    /**
     * @brief Loads key-value pairs into a hash map from a .ini file.
     * @throws std::runtime_error if the file cannot be opened or if it contains a syntax error.
     * @param fp File to read
     * @param store Hash map to store into.
     */
    void load_ini(const std::string &fp, std::unordered_map<std::string, std::string> &store);
}

#endif