package io.github.jgame.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * Representation of the client for the {@link UDPServer}
 */
public class UDPClientHandler {
    InetAddress address;
    int port;
    UDPServer factory;

    /**
     * New Client!
     *
     * @param listenHost Host the client is at
     * @param listenPort Port the client is at
     * @param parent     UDPServer client is connected to
     */
    public UDPClientHandler(InetAddress listenHost, int listenPort, UDPServer parent) {
        address = listenHost;
        port = listenPort;
        factory = parent;
    }

    /**
     * Send packet
     *
     * @param datagram Packet to send
     * @throws IOException Sending the packet over the network may fail
     */
    public void send(HashMap<String, Object> datagram) throws IOException {
        factory.send(datagram, address, port);
    }

    /**
     * Handler for any packet that gets sent to us
     *
     * @param packetDict Packet that was sent
     * @param datagram Raw {@link DatagramPacket}
     */
    public void parse(HashMap<String, Object> packetDict, DatagramPacket datagram) {

    }

    /**
     * Sends a serverShutdown event to the client.
     *
     * @throws IOException Sending the message may fail.
     */
    public void shutdown() throws IOException {
        HashMap<String, Object> shutdownMsg = new HashMap<>();
        shutdownMsg.put("action", "serverShutdown");
        send(shutdownMsg);
    }
}
