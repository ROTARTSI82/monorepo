package io.github.jgame.net.udp;

import io.github.jgame.Constants;
import io.github.jgame.logging.GenericLogger;
import io.github.jgame.net.NetUtils;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;
import static io.github.jgame.util.StringManager.fmt;

public class UDPServer {
    private DatagramSocket socket;
    private InetAddress address;
    private Logger logger;
    private int port;

    private LinkedList<String> verifiedByMe = new LinkedList<>();
    private HashMap<String, VerifyPacket> pendingPackets = new HashMap<>();
    private HashMap<String, UDPClientHandler> clients = new HashMap<>();
    private HashMap<String, Integer> serialTable;
    private HashMap<Integer, String> deserialTable;

    private int clientLimit;

    /**
     * Create a new UDP server at the specified address
     *
     * @param host       Host to bind to
     * @param listenPort port to bind to
     * @param maxClients Maximum number of clients
     * @throws UnknownHostException     Invalid {@code hostname}
     * @throws SocketException          Error opening DatagramSocket
     * @throws IllegalArgumentException Invalid actionID
     */
    public UDPServer(String host, int listenPort, int maxClients) throws UnknownHostException, SocketException {
        address = InetAddress.getByName(host);
        port = listenPort;
        clientLimit = maxClients;
        socket = new DatagramSocket(listenPort, address);
        logger = Logger.getLogger(this.getClass().getName());

        serialTable = getActionTable();
        deserialTable = new HashMap<>();
        for (String action : serialTable.keySet()) {
            int actionID = serialTable.get(action);

            if (actionID > 0 && actionID <= 0xffff) {
                deserialTable.put(actionID, action);
            } else {
                throw new IllegalArgumentException("actionIDs need to be between 0x0000 and 0xffff");
            }
        }
    }

    public HashMap<String, Integer> getActionTable() {
        return Constants.BUILTIN_ACTIONS;
    }

    public void send(HashMap<String, Object> datagram, InetAddress datAddress, int datPort) throws IOException {
        logger.finest(fmt(JGameStr.getString("net.sendMSG"), socket.getInetAddress(), port,
                datAddress, datPort, datagram));
        byte[] bytes = NetUtils.serialize(datagram, serialTable);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, datAddress, datPort);
        socket.send(packet);
    }

    public void sendToAll(HashMap<String, Object> datagram) {
        for (UDPClientHandler client : clients.values()) {
            try {
                send(datagram, client.address, client.port);
            } catch (IOException e) {
                logger.warning("Failed to send packet:\n" + GenericLogger.getStackTrace(e));
            }
        }
    }

    public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {

    }

    public void update() throws IOException {
        byte[] bytes = new byte[Constants.NET_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        socket.receive(packet);

        String packAddr = packet.getAddress().toString() + ":" + packet.getPort();
        if (!clients.containsKey(packAddr)) {
            if (clients.size() >= clientLimit) {
                HashMap<String, Object> kickMsg = new HashMap<>();
                kickMsg.put("action", "kick");
                kickMsg.put("reason", JGameStr.getString("net.alreadyFullMsg"));

                send(kickMsg, packet.getAddress(), packet.getPort());
                return;
            }
            clients.put(packAddr, new UDPClientHandler(packet.getAddress(), packet.getPort(), this));
            logger.info(fmt(JGameStr.getString("net.newClient"), address, port, packAddr));
        }

        HashMap<String, Object> packetDict = NetUtils.deserialize(packet.getData(), deserialTable);
        if (packetDict == null) {
            return;
        }
        logger.finest(fmt(JGameStr.getString("net.recvMSG"), socket.getInetAddress(), port,
                packet.getAddress(), packet.getPort(), packetDict));

        String action = (String) packetDict.get("action");
        if (action != null) {
            switch (action) {
                case "clientShutdown": {
                    onClientShutdown(packet);
                    return;
                }
                case "verifySend": {
                    String id = (String) packetDict.get("id");
                    if (!verifiedByMe.contains(id)) {
                        logger.finest(fmt(JGameStr.getString("net.UDPServer.confirmServer"),
                                address, port, id));
                        parse(NetUtils.datFromObject(packetDict.get("data")), packet);
                        verifiedByMe.add(id);
                    } else {
                        logger.fine(fmt(JGameStr.getString("net.UDP.duplicatePacket"), address, port, id));
                    }
                    HashMap<String, Object> rawSend = new HashMap<>();
                    rawSend.put("action", "confirmPacket");
                    rawSend.put("id", id);
                    send(rawSend, packet.getAddress(), packet.getPort());
                    return;
                }
                case "confirmPacket": {
                    String id = (String) packetDict.get("id");
                    if (pendingPackets.containsKey(id)) {
                        pendingPackets.get(id).onConfirm();
                        pendingPackets.remove(id);
                    }
                    logger.finest(fmt(JGameStr.getString("net.UDP.confirmedPacket"), address, port, id));
                    return;
                }
            }
        }
        clients.get(packAddr).parse(packetDict, packet);
        parse(packetDict, packet);
    }

    public void onClientShutdown(DatagramPacket packet) {
        clients.remove(packet.getAddress().toString() + ":" + packet.getPort());
    }

    public void shutdown() {
        for (UDPClientHandler client : clients.values()) {
            try {
                client.shutdown();
            } catch (IOException e) {
                logger.log(Level.WARNING, JGameStr.getString("net.shutdownFail"), e);
            }
        }

        for (VerifyPacket p : pendingPackets.values()) {
            p.stop();
        }
    }

    public void addVerifyPacket(HashMap<String, Object> datagram, int frequency, double backoff,
                                InetAddress host, int port) {
        VerifyPacket packet = new VerifyPacket(datagram, frequency, backoff, host, port, this);
        pendingPackets.put(packet.id, packet);
    }
}
