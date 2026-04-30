#pragma once

namespace mc {
    class tcp_server {
    public:
        int sock = -1;

        tcp_server(const char *addr, const char *port);
        void accept();
        ~tcp_server();
    };

}
