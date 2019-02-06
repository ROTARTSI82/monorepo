package io.github.jgame.net;

import io.github.jgame.Constants;
import javafx.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class UDPServer {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private HashMap<Pair<InetAddress, Integer>, ClientHandler> clients = new HashMap<>();

    public UDPServer(String host, int listenPort) throws Exception {
        address = InetAddress.getByName(host);
        port = listenPort;
        socket = new DatagramSocket(listenPort, address);
    }

    public void send(HashMap<String, Object> datagram, InetAddress address, int port) throws Exception {
        byte[] bytes = NetUtils.serialize(datagram);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
        socket.send(packet);
    }

    public void parse(DatagramPacket datagram) {

    }

    public void update() throws IOException {
        byte[] bytes = new byte[Constants.NET_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        socket.receive(packet);

        Pair<InetAddress, Integer> addressPair = new Pair<>(packet.getAddress(), packet.getPort());
        if (!clients.containsKey(addressPair)) {
            clients.put(addressPair, new ClientHandler(addressPair, this));
        }
        clients.get(addressPair).parse(packet);
        parse(packet);
    }
}
