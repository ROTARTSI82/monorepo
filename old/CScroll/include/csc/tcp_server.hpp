/**
 * @file tcp_server.hpp
 * @author your name (you@domain.com)
 * @brief 
 * @version 0.1
 * @date 2021-09-26
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#pragma once

#ifndef CSC_TCP_SERVER_HPP
#define CSC_TCP_SERVER_HPP

#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#include <vector>
#include <string>

namespace csc {
    struct ClientData {
        int sock;
    };

    class TcpServer {
    private:
        int sock;

        int update_it = 0;
        std::vector<ClientData> clients;

    public:
        TcpServer() = delete;
        TcpServer(const char *ip, const char *port);
        ~TcpServer();

        void tickAccept();
        void tickClients(int num);

        TcpServer(const TcpServer &rhs) = delete;
        TcpServer &operator=(const TcpServer &rhs) = delete;


    };
}

#endif