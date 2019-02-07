package io.github.jgame.net;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

public class ClientHandler {
    InetAddress address;
    int port;
    UDPServer factory;

    public ClientHandler(InetAddress listenHost, int listenPort, UDPServer parent) {
        address = listenHost;
        port = listenPort;
        factory = parent;
    }

    public void send(HashMap<String, Object> datagram) throws Exception {
        factory.send(datagram, address, port);
    }

    public void parse(HashMap<String, Object> packetDict, DatagramPacket datagram) {

    }
}
