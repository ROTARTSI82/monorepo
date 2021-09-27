#include "csc/properties.hpp"

#include <fstream>

namespace csc {
    ServerProperties load_props() {
        ServerProperties ret;

        std::ifstream fp{"server.properties"};
        

        return ret;
    }
}