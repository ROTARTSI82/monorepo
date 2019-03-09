package io.github.jgame.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

public class UDPClientHandler {
    InetAddress address;
    int port;
    UDPServer factory;

    public UDPClientHandler(InetAddress listenHost, int listenPort, UDPServer parent) {
        address = listenHost;
        port = listenPort;
        factory = parent;
    }

    public void send(HashMap<String, Object> datagram) throws IOException {
        factory.send(datagram, address, port);
    }

    public void parse(HashMap<String, Object> packetDict, DatagramPacket datagram) {

    }

    public void shutdown() throws IOException {
        HashMap<String, Object> shutdownMsg = new HashMap<>();
        shutdownMsg.put("action", "serverShutdown");
        send(shutdownMsg);
    }
}
