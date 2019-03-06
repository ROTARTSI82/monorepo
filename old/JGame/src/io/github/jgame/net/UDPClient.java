package io.github.jgame.net;

import io.github.jgame.Constants;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

public class UDPClient {
    private Logger logger;
    private DatagramSocket socket;
    private InetAddress host;
    private int port;

    private HashMap<String, VerifyPacket> pendingPackets = new HashMap<>();
    private LinkedList<String> verifiedByMe = new LinkedList<>();

    private HashMap<String, Integer> serialTable;
    private HashMap<Integer, String> deserialTable;

    private HashMap<String, Object> filler = new HashMap<>();

    /**
     * Create a new UDP client at a random available address and listen to the specified port and host.
     *
     * @param listenHost Host to connect to
     * @param listenPort Port to connect to
     * @throws UnknownHostException     Invalid {@code hostname}
     * @throws SocketException          Error opening DatagramSocket
     * @throws IllegalArgumentException Invalid actionID
     */
    public UDPClient(String listenHost, int listenPort) throws UnknownHostException, SocketException {
        host = InetAddress.getByName(listenHost);
        port = listenPort;
        socket = new DatagramSocket();
        logger = Logger.getLogger(this.getClass().getName());

        filler.put("action", "filler");

        serialTable = getActionTable();
        deserialTable = new HashMap<>();
        for (String action : serialTable.keySet()) {
            int actionID = serialTable.get(action);

            if (actionID > 0 && actionID <= 0xffff) {
                deserialTable.put(actionID, action);
            } else {
                throw new IllegalArgumentException(JGameStr.getString("net.invalidActionID"));
            }
        }
    }

    public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {

    }

    public void shutdown() throws IOException {
        HashMap<String, Object> shutdownMsg = new HashMap<>();
        shutdownMsg.put("action", "clientShutdown");
        send(shutdownMsg);

        for (VerifyPacket p : pendingPackets.values()) {
            p.stop();
        }
    }

    public HashMap<String, Integer> getActionTable() {
        return Constants.BUILTIN_ACTIONS;
    }

    public void update() throws IOException {
        byte[] recv = new byte[Constants.NET_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(recv, recv.length);
        socket.receive(packet);
        HashMap<String, Object> packetDict = NetUtils.deserialize(packet.getData(), deserialTable);
        if (packetDict == null) {
            return;
        }
        logger.finest(fmt(JGameStr.getString("net.recvMSG"), "?", "?", host, port, packetDict));
        String action = (String) packetDict.get("action");
        if (action != null) {
            switch (action) {
                case "serverShutdown": {
                    onServerShutdown();
                    return;
                }
                case "verifySend": {
                    String id = (String) packetDict.get("id");
                    if (!verifiedByMe.contains(id)) {
                        logger.finest(fmt(JGameStr.getString("net.UDPClient.confirmServer"),
                                host, port, id));
                        parse(NetUtils.datFromObject(packetDict.get("data")), packet);
                        verifiedByMe.add(id);
                    } else {
                        logger.fine(fmt(JGameStr.getString("net.UDP.duplicatePacket"), host, port, id));
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
                        logger.finest(fmt(JGameStr.getString("net.UDP.confirmedPacket"), host, port, id));
                        pendingPackets.get(id).onConfirm();
                        pendingPackets.remove(id);
                    } else {
                        logger.fine(fmt(JGameStr.getString("net.UDP.outdatedPacket"), host, port, id));
                    }
                    return;
                }
            }
        }
        parse(packetDict, packet);
    }

    public void onServerShutdown() {

    }

    public void onKick(String reason) {

    }

    public void addVerifyPacket(HashMap<String, Object> datagram, int frequency, double backoff) {
        VerifyPacket packet = new VerifyPacket(datagram, frequency, backoff);
        pendingPackets.put(packet.id, packet);
    }

    public void send(HashMap<String, Object> datagram) throws IOException {
        logger.finest(fmt(JGameStr.getString("net.sendMSG"), "?", "?", host, port, datagram));
        byte[] bytes = NetUtils.serialize(datagram, serialTable);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
        socket.send(packet);
    }

    public class VerifyPacket {
        String id;
        HashMap<String, Object> rawSend;

        final Timer timer;

        int backoff;

        volatile boolean verified = false;
        volatile boolean terminate = false;
        volatile boolean hasSent = false;

        public VerifyPacket(HashMap<String, Object> datagram, int frequency, double multiplier) {
            if (!datagram.containsKey("action")) {
                datagram.put("action", null);
            }

            rawSend = new HashMap<>();
            rawSend.put("action", "verifySend");
            rawSend.put("data", datagram);

            id = UUID.randomUUID().toString();
            rawSend.put("id", id);

            backoff = frequency;
            timer = new Timer();
            timer.schedule(getTask(multiplier), frequency);
        }

        public TimerTask getTask(double multiplier) {
            return new TimerTask() {
                @Override
                public void run() {
                    synchronized (VerifyPacket.class) {
                        if ((verified && hasSent) || terminate) {
                            stop();
                            this.cancel();
                            return;
                        } else {
                            try {
                                send(rawSend);
                                hasSent = true;
                            } catch (IOException err) {
                                logger.log(Level.WARNING, fmt(JGameStr.getString("net.UDP.resendFail"), rawSend), err);
                            }
                        }

                        try {
                            send(filler);
                        } catch (IOException e) {
                            logger.log(Level.WARNING, JGameStr.getString("net.UDP.fillerFail"), e);
                        }

                        backoff *= multiplier;
                        try {
                            timer.schedule(getTask(multiplier), backoff);
                            logger.finest(fmt(JGameStr.getString("net.UDP.reschedule"), rawSend, backoff));
                        } catch (IllegalStateException e) {
                            logger.log(Level.WARNING, fmt(JGameStr.getString("net.UDP.illegalTimer"), rawSend), e);
                        }
                        this.cancel();
                    }
                }
            };
        }

        public void onConfirm() {
            verified = true;
        }

        public synchronized void stop() {
            logger.fine(fmt(JGameStr.getString("net.UDP.threadStop"), rawSend));
            timer.cancel();
            timer.purge();
            terminate = true;
        }
    }
}
