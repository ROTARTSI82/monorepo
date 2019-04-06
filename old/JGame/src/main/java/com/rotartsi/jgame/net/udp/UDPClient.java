package com.rotartsi.jgame.net.udp;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.net.NetUtils;
import com.rotartsi.jgame.util.StringManager;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import static com.rotartsi.jgame.Constants.JGameStr;

/**
 * Client implementation of UDP.
 */
public class UDPClient {
    /**
     * Internal logger object used to log events.
     */
    private Logger logger;

    /**
     * The socket that the client is binded to.
     */
    private DatagramSocket socket;

    /**
     * The port that the client is connected to.
     */
    private InetAddress host;

    /**
     * The port that the client is connected to.
     */
    private int port;

    /**
     * List of UUIDs of verified packets that have already been handled.
     * <p>
     * When we get a packet with a UUID in this list, we do not call the {@link #parse} handler
     * and simply send the confirm message.
     */
    private HashMap<String, VerifyPacket> pendingPackets = new HashMap<>();

    /**
     * Active {@link VerifyPacket}s that need to be updated.
     */
    private LinkedList<String> verifiedByMe = new LinkedList<>();

    /**
     * Table used to serialize actions. See {@link NetUtils}.serialize()
     */
    private HashMap<String, Integer> serialTable;

    /**
     * A reversed copy of the {@link #serialTable} (values are keys and keys are values).
     *
     * Therefore, the serial table must not contain any duplicate values for this to work properly.
     *
     * Used for deserialization of actions.
     */
    private HashMap<Integer, String> deserialTable;


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

    /**
     * Event handler for all packets sent to this address.
     *
     * @param datagram HashMap that was sent
     * @param packet Raw {@link DatagramPacket}
     */
    public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {

    }

    /**
     * Send the clientShutdown event and stop all {@link #pendingPackets}
     *
     * @throws IOException Sending packets may fail.
     */
    public void shutdown() throws IOException {
        HashMap<String, Object> shutdownMsg = new HashMap<>();
        shutdownMsg.put("action", "clientShutdown");
        send(shutdownMsg);

        for (VerifyPacket p : pendingPackets.values()) {
            p.stop();
        }
    }

    /**
     * Used in constructor to determine action table. Action tables are used to serialize actions. See
     * {@link NetUtils}.serialize()
     *
     * @return Table
     */
    public HashMap<String, Integer> getActionTable() {
        return Constants.BUILTIN_ACTIONS;
    }

    /**
     * Handle serverShutdown and kick events. Forward others to the {@link #parse} handler.
     *
     * @throws IOException Reading the input and sending messages may fail.
     */
    public void update() throws IOException {
        byte[] recv = new byte[Constants.NET_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(recv, recv.length);
        socket.receive(packet);
        HashMap<String, Object> packetDict = NetUtils.deserialize(packet.getData(), deserialTable);
        if (packetDict == null) {
            return;
        }
        logger.finest(StringManager.fmt(JGameStr.getString("net.recvMSG"), "?", "?", host, port, packetDict));
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
                        logger.finest(StringManager.fmt(JGameStr.getString("net.UDPClient.confirmServer"),
                                host, port, id));
                        parse(NetUtils.datFromObject(packetDict.get("data")), packet);
                        verifiedByMe.add(id);
                    } else {
                        logger.fine(StringManager.fmt(JGameStr.getString("net.UDP.duplicatePacket"), host, port, id));
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
                        logger.finest(StringManager.fmt(JGameStr.getString("net.UDP.confirmedPacket"), host, port, id));
                        pendingPackets.get(id).onConfirm();
                        pendingPackets.remove(id);
                    } else {
                        logger.fine(StringManager.fmt(JGameStr.getString("net.UDP.outdatedPacket"), host, port, id));
                    }
                    return;
                }
            }
        }
        parse(packetDict, packet);
    }

    /**
     * Handler for serverShutdown events
     */
    public void onServerShutdown() {

    }

    /**
     * Handler for kick events.
     *
     * @param reason Kicked for reason.
     */
    public void onKick(String reason) {

    }

    /**
     * Add a VerifyPacket to {@link #pendingPackets}. (In other words, send the packet, and make sure it arrives!)
     * See {@link VerifyPacket}
     *
     * @param datagram Packet to send
     * @param frequency Initial delay
     * @param backoff Exponential backoff
     */
    public void addVerifyPacket(HashMap<String, Object> datagram, int frequency, double backoff) {
        VerifyPacket packet = new VerifyPacket(datagram, frequency, backoff, this);
        pendingPackets.put(packet.id, packet);
    }

    /**
     * Send a message to the server
     *
     * @param datagram Packet to send
     *
     * @throws IOException Sending packets may fail.
     */
    public void send(HashMap<String, Object> datagram) throws IOException {
        logger.finest(StringManager.fmt(JGameStr.getString("net.sendMSG"), "?", "?", host, port, datagram));
        byte[] bytes = NetUtils.serialize(datagram, serialTable);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
        socket.send(packet);
    }
}
