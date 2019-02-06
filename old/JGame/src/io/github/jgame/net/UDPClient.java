package io.github.jgame.net;

import io.github.jgame.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress host;
    private int port;

    public UDPClient(String listenHost, int listenPort) throws Exception {
        host = InetAddress.getByName(listenHost);
        port = listenPort;
        socket = new DatagramSocket();
    }

    public static String extractString(DatagramPacket packet) {
        return new String(packet.getData()).replace("\0", "");
    }

    public void parse(DatagramPacket datagram) {

    }

    public void update() throws IOException {
        byte[] recv = new byte[Constants.NET_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(recv, recv.length);
        socket.receive(packet);
        parse(packet);
    }

    public void send(HashMap<String, Object> datagram) throws IOException {
        byte[] bytes = NetUtils.serialize(datagram);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
        socket.send(packet);
    }
}
