#pragma once

#include "mc/async.hpp"

namespace mc {

    class tcp_server {
    public:
        thread_pool *pool;
        int sock = -1;

        tcp_server(const char *addr, const char *port, thread_pool *pool);
        ~tcp_server();

        static pool_future accept_loop(tcp_server *);
    };
}
