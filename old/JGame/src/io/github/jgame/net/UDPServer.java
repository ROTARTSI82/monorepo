package io.github.jgame.net;

import io.github.jgame.Constants;
import io.github.jgame.logging.GenericLogger;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

public class UDPServer {
    private DatagramSocket socket;
    private InetAddress address;
    private Logger logger;
    private int port;

    private LinkedList<Long> verifiedByMe = new LinkedList<>();
    private HashMap<Long, VerifyPacket> pendingPackets = new HashMap<>();
    private HashMap<Pair<InetAddress, Integer>, ClientHandler> clients = new HashMap<>();

    public UDPServer(String host, int listenPort) throws Exception {
        address = InetAddress.getByName(host);
        port = listenPort;
        socket = new DatagramSocket(listenPort, address);
        logger = Logger.getLogger(this.getClass().getName());
    }

    public void send(HashMap<String, Object> datagram, InetAddress address, int port) throws Exception {
        byte[] bytes = NetUtils.serialize(datagram);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
        socket.send(packet);
    }

    public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {

    }

    public void update() throws Exception {
        byte[] bytes = new byte[Constants.NET_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        socket.receive(packet);

        Pair<InetAddress, Integer> addressPair = new Pair<>(packet.getAddress(), packet.getPort());
        if (!clients.containsKey(addressPair)) {
            clients.put(addressPair, new ClientHandler(addressPair, this));
        }
        clients.get(addressPair).parse(packet);

        HashMap<String, Object> packetDict = NetUtils.deserialize(packet.getData());
        if (packetDict == null) {
            return;
        }
        String action = (String) packetDict.get("action");
        switch (action) {
            case "verifySend": {
                Long id = (Long) packetDict.get("id");
                if (!verifiedByMe.contains(id)) {
                    parse(NetUtils.datFromObject(packetDict.get("data")), packet);
                    verifiedByMe.add(id);
                }
                HashMap<String, Object> rawSend = new HashMap<>();
                rawSend.put("action", "confirm");
                rawSend.put("id", id);
                send(rawSend, packet.getAddress(), packet.getPort());
                return;
            }
            case "confirm": {
                Long id = (Long) packetDict.get("id");
                pendingPackets.get(id).onConfirm();
                return;
            }
        }
        parse(packetDict, packet);
    }

    public void addVerifyPacket(HashMap<String, Object> datagram, int frequency, InetAddress host, int port) {
        VerifyPacket packet = new VerifyPacket(datagram, frequency, host, port);
        pendingPackets.put(packet.id, packet);
    }

    public class VerifyPacket implements ActionListener {
        Timer timer;
        long id;
        HashMap<String, Object> rawSend;
        InetAddress myHost;
        int myPort;

        boolean verified = false;
        boolean hasSent = false;

        public VerifyPacket(HashMap<String, Object> datagram, int frequency, InetAddress host, int port) {
            rawSend = new HashMap<>();
            rawSend.put("action", "verifySend");
            rawSend.put("data", datagram);
            id = System.currentTimeMillis();
            rawSend.put("id", id);
            myHost = host;
            myPort = port;

            timer = new Timer(frequency, this);  // Frequency in milliseconds
            timer.start();
        }

        public void onConfirm() {
            verified = true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (verified && hasSent) {
                timer.stop();
            } else {
                try {
                    send(rawSend, myHost, myPort);
                    hasSent = true;
                } catch (Exception err) {
                    logger.info(String.format("Failed to resend %s:\n%s", rawSend,
                            GenericLogger.getStackTrace(err)));
                }
            }
        }
    }
}
