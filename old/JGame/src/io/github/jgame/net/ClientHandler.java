package io.github.jgame.net;

import javafx.util.Pair;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

public class ClientHandler {
    InetAddress address;
    int port;
    UDPServer factory;

    public ClientHandler(Pair<InetAddress, Integer> listenAddr, UDPServer parent) {
        address = listenAddr.getKey();
        port = listenAddr.getValue();
        factory = parent;
    }

    public void send(HashMap<String, Object> datagram) throws Exception {
        factory.send(datagram, address, port);
    }

    public void parse(DatagramPacket datagram) {

    }
}
