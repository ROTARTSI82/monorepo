package io.github.jgame.net;

import io.github.jgame.Constants;
import io.github.jgame.logging.GenericLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

public class UDPClient {
    private Logger logger;
    private DatagramSocket socket;
    private InetAddress host;
    private int port;

    private HashMap<Long, VerifyPacket> pendingPackets = new HashMap<>();
    private LinkedList<Long> verifiedByMe = new LinkedList<>();

    public UDPClient(String listenHost, int listenPort) throws Exception {
        host = InetAddress.getByName(listenHost);
        port = listenPort;
        socket = new DatagramSocket();
        logger = Logger.getLogger(this.getClass().getName());
    }

    public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {

    }

    public void update() throws IOException {
        byte[] recv = new byte[Constants.NET_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(recv, recv.length);
        socket.receive(packet);
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
                send(rawSend);
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

    public void addVerifyPacket(HashMap<String, Object> datagram, int frequency) {
        VerifyPacket packet = new VerifyPacket(datagram, frequency);
        pendingPackets.put(packet.id, packet);
    }

    public class VerifyPacket implements ActionListener {
        Timer timer;
        long id;
        HashMap<String, Object> rawSend;

        boolean verified = false;
        boolean hasSent = false;

        public VerifyPacket(HashMap<String, Object> datagram, int frequency) {
            rawSend = new HashMap<>();
            rawSend.put("action", "verifySend");
            rawSend.put("data", datagram);
            id = System.currentTimeMillis();
            rawSend.put("id", id);

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
                    send(rawSend);
                    hasSent = true;
                } catch (IOException err) {
                    logger.info(String.format("Failed to resend %s:\n%s", rawSend,
                            GenericLogger.getStackTrace(err)));
                }
            }
        }
    }

    public void send(HashMap<String, Object> datagram) throws IOException {
        byte[] bytes = NetUtils.serialize(datagram);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
        socket.send(packet);
    }
}
