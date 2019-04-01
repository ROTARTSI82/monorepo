package io.github.jgame.net.tcp;

import io.github.jgame.Constants;
import io.github.jgame.net.NetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;
import static io.github.jgame.util.StringManager.fmt;

/**
 * Client for TCP protocol.
 */
public class TCPClient {
    /**
     * The socket. Contains the host and the port. See {@link Socket}
     */
    private Socket socket;

    /**
     * Output stream. Write to this stream to send data to the server.
     */
    private PrintWriter out;

    /**
     * Input stream. Read this stream to read messages from the server.
     */
    private BufferedReader in;

    /**
     * Internal logger object used for logging events.
     */
    private Logger logger;

    /**
     * Table used to serialize actions. See {@link NetUtils}.serialize()
     */
    private HashMap<String, Integer> serialTable;

    /**
     * A reversed copy of the {@link #serialTable} (values are keys and keys are values).
     * <p>
     * Therefore, the serial table must not contain any duplicate values for this to work properly.
     * <p>
     * Used for deserialization of actions.
     */
    private HashMap<Integer, String> deserialTable;

    /**
     * Host a TCP client at a random available address and connect to specified {@code hostname} and {@code portNum}
     *
     * @param hostname Host to connect to
     * @param portNum  Port to connect to
     * @throws UnknownHostException     Invalid {@code hostname}
     * @throws IOException              Error opening PrintWriter or BufferedReader or socket
     * @throws IllegalArgumentException Invalid actionID
     */
    public TCPClient(String hostname, int portNum) throws UnknownHostException, IOException {
        logger = Logger.getLogger(this.getClass().getName());
        socket = new Socket(hostname, portNum);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
     * Send a message to the server
     *
     * @param datagram Message to send
     */
    public void send(HashMap<String, Object> datagram) {
        logger.finest(fmt(JGameStr.getString("net.sendMSG"), "?", "?",
                socket.getInetAddress(), socket.getPort(), datagram));
        String send = Base64.getEncoder().encodeToString(NetUtils.serialize(datagram, serialTable));
        out.println(send);
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
     * Handle serverShutdown and kick events.
     *
     * @throws IOException Closing the connection may fail.
     */
    public void update() throws IOException {
        HashMap<String, Object> dat = NetUtils.deserialize(Base64.getDecoder().decode(in.readLine()),
                deserialTable);
        if (dat == null) {
            return;
        }
        logger.finest(fmt(JGameStr.getString("net.recvMSG"), "?", "?", socket.getInetAddress(),
                socket.getPort(), dat));
        String action = (String) dat.get("action");
        if (action != null) {
            switch (action) {
                case "kick": {
                    onKick((String) dat.get("reason"));
                    return;
                }
                case "serverShutdown": {
                    onServerShutdown();
                    return;
                }
            }
        }
        parse(dat);
    }

    /**
     * Handler for kick events.
     *
     * @param reason String reason
     */
    public void onKick(String reason) {

    }

    /**
     * Handler for serverShutdown events.
     */
    public void onServerShutdown() {

    }

    /**
     * Handler for all messages sent here.
     *
     * @param datagram Message
     */
    public void parse(HashMap<String, Object> datagram) {

    }

    /**
     * Close all connections and send the clientShutdown message.
     * @throws IOException Closing connection may fail.
     */
    public void shutdown() throws IOException {
        HashMap<String, Object> leaveMsg = new HashMap<>();
        leaveMsg.put("action", "clientShutdown");
        send(leaveMsg);

        socket.close();
        in.close();
        out.close();
    }
}
