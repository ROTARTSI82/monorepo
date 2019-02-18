package io.github.jgame.net;

import io.github.jgame.Constants;
import io.github.jgame.logging.GenericLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Logger;

public class UDPClient {
    private Logger logger;
    private DatagramSocket socket;
    private InetAddress host;
    private int port;

    private HashMap<String, VerifyPacket> pendingPackets = new HashMap<>();
    private LinkedList<String> verifiedByMe = new LinkedList<>();

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
        logger.finest(String.format("[?->%s:%s] Got packet %s from %s:%s", host, port, packetDict,
                packet.getAddress(), packet.getPort()));
        String action = (String) packetDict.get("action");
        switch (action) {
            case "verifySend": {
                String id = (String) packetDict.get("id");
                if (!verifiedByMe.contains(id)) {
                    logger.finest(String.format("[?->%s:%s] Packet<id=%s> from server was verified.",
                            host, port, id));
                    parse(NetUtils.datFromObject(packetDict.get("data")), packet);
                    verifiedByMe.add(id);
                } else {
                    logger.fine(String.format("[?->%s:%s] Got duplicate Packet<id=%s>", host, port, id));
                }
                HashMap<String, Object> rawSend = new HashMap<>();
                rawSend.put("action", "confirmPacket");
                rawSend.put("id", id);
                send(rawSend);
                return;
            }
            case "kick": {
                onKick((String) packetDict.get("reason"));
                return;
            }
            case "confirmPacket": {
                String id = (String) packetDict.get("id");
                if (pendingPackets.containsKey(id)) {
                    logger.finest(String.format("[?->%s:%s] Packet<id=%s> was confirmed.", host, port, id));
                    pendingPackets.get(id).onConfirm();
                    pendingPackets.remove(id);
                } else {
                    logger.fine(String.format("[?->%s:%s] Packet<id=%s> doesn't exist!", host, port, id));
                }
                return;
            }
        }
        parse(packetDict, packet);
    }

    public void onKick(String reason) {

    }

    public void addVerifyPacket(HashMap<String, Object> datagram, int frequency) {
        VerifyPacket packet = new VerifyPacket(datagram, frequency);
        pendingPackets.put(packet.id, packet);
    }

    public void send(HashMap<String, Object> datagram) throws IOException {
        logger.finest(String.format("[?->%s:%s] Sent %s", host, port, datagram));
        byte[] bytes = NetUtils.serialize(datagram);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
        socket.send(packet);
    }

    public class VerifyPacket {
        String id;
        HashMap<String, Object> rawSend;

        final TimerTask trySend;
        final Timer timer;

        volatile boolean verified = false;
        volatile boolean hasSent = false;

        public VerifyPacket(HashMap<String, Object> datagram, int frequency) {
            rawSend = new HashMap<>();
            rawSend.put("action", "verifySend");
            rawSend.put("data", datagram);
            id = UUID.randomUUID().toString();
            rawSend.put("id", id);

            timer = new Timer();
            trySend = new TimerTask() {
                @Override
                public void run() {
                    synchronized (VerifyPacket.class) {
                        if (verified && hasSent) {
                            trySend.cancel();
                            timer.cancel();
                            timer.purge();
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
            };
            timer.schedule(trySend, 0, frequency);
        }

        public void onConfirm() {
            verified = true;
        }
    }
}
