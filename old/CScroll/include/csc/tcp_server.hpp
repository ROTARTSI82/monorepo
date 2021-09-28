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
#include <tuple>

namespace csc {

    enum class ClientState : uint8_t {
        handshake, status = 1, login = 2, play
    };

    enum class HandshakePid : uint8_t { handshaking = 0x00, legacyServerPing = 0xFE };
    enum class CliBoundStatusPid : uint8_t { response = 0x00, pong = 0x01 };
    enum class ServBoundStatusPid : uint8_t { request = 0x00, ping = 0x01 };

    struct ClientData {
        int sock;

        ClientState state = ClientState::handshake;
        bool compressed = false;
    };

    struct HandlingInfo {
        int8_t *dat, *origPacket;
        int id;
        int len, rsize;
        int cliRef;
    };


    class CliBoundPacketFactory {
    private:
        int8_t *store;
        unsigned capacity;
        uint8_t headroom;

    public:
        CliBoundPacketFactory() = delete;
        CliBoundPacketFactory(int id, unsigned size);
        ~CliBoundPacketFactory();

        inline int8_t *get();

        std::pair<int8_t *, unsigned> construct(int newsize);
    };

    class TcpServer {
    private:
        int sock;
        std::vector<ClientData> clients;

        void handleHandshake(HandlingInfo &info);
        void handleStatus(HandlingInfo &info);
        void handleLogin(HandlingInfo &info);

    public:
        TcpServer() = delete;
        TcpServer(const char *ip, const char *port);
        ~TcpServer();

        void tickAccept();
        void tickClient(int cliRef);

        inline int numClients() { return clients.size(); }

        void dropConnection(int cliRef);

        TcpServer(const TcpServer &rhs) = delete;
        TcpServer &operator=(const TcpServer &rhs) = delete;


    };
}

#endif