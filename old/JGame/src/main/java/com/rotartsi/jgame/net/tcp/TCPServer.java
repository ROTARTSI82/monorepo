package com.rotartsi.jgame.net.tcp;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.net.NetUtils;
import com.rotartsi.jgame.util.StringManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;

/**
 * TCP Server implementation (handles clients)
 */
public class TCPServer {

    /**
     * The socket that the server is binded to. See {@link Socket}
     */
    public ServerSocket serverSocket;

    /**
     * The list of active clients that are connected.
     */
    private HashMap<String, TCPClientHandler> clients = new HashMap<>();

    /**
     * Table used to serialize actions. See {@link NetUtils}.serialize()
     */
    public HashMap<String, Integer> serialTable;

    /**
     * Internal logger object used to log events.
     */
    private Logger logger = Logger.getLogger(TCPServer.class);

    /**
     * A reversed copy of the {@link #serialTable} (values are keys and keys are values).
     * <p>
     * Therefore, the serial table must not contain any duplicate values for this to work properly.
     * <p>
     * Used for deserialization of actions.
     */
    public HashMap<Integer, String> deserialTable;
    /**
     * The number of clients we accept before we start kicking them.
     */
    private int clientLimit;

    /**
     * Host a TCP server at a specific address
     *
     * @param hostname   Host to bind the server to
     * @param portNum    Port to bind the server to
     * @param maxClients maximum number of clients
     * @throws java.net.UnknownHostException Invalid host is passed in {@code hostname}
     * @throws IOException                   Error opening socket
     * @throws IllegalArgumentException      If an actionID is out of range.
     */
    public TCPServer(String hostname, int portNum, int maxClients) throws UnknownHostException, IOException {
        clientLimit = maxClients;
        serverSocket = new ServerSocket(portNum, 50, InetAddress.getByName(hostname));

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
     * Send a message to all clients globally.
     *
     * @param datagram Message
     */
    public void sendToAll(HashMap<String, Object> datagram) {
        for (TCPClientHandler handler : clients.values()) {
            handler.send(datagram);
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
     * Remove client. Client may have disconnected or something.
     *
     * @param client Client to remove
     */
    public void removeClient(TCPClientHandler client) {
        clients.remove(client.toString());
    }

    /**
     * Accept new clients and call the update() functions of all {@link TCPClientHandler}s.
     *
     * @throws IOException Accepting the client may cause errors.
     */
    public void update() throws IOException {
        Socket client = serverSocket.accept();
        String key = StringManager.fmt("%s:%s", client.getInetAddress().toString(), client.getPort());
        TCPClientHandler handler = new TCPClientHandler(client, this);

        if (clients.size() >= clientLimit) {
            HashMap<String, Object> kickMsg = new HashMap<>();
            kickMsg.put("action", "kick");
            kickMsg.put("reason", JGameStr.getString("net.alreadyFullMsg"));
            handler.send(kickMsg);
            logger.info(fmt(JGameStr.getString("net.TCPServer.alreadyFullLog"), key));
            return;
        }
        if (!clients.containsKey(key)) {
            clients.put(key, handler);
            logger.info(StringManager.fmt(JGameStr.getString("net.newClient"), serverSocket.getInetAddress(),
                    serverSocket.getLocalPort(), key));
        } else {
            logger.trace(JGameStr.getString("net.TCPServer.alreadyExists"));
        }


        for (TCPClientHandler cli : clients.values()) {
            try {
                cli.update();
            } catch (IOException e) {
                logger.warn(JGameStr.getString("net.TCPServer.updateFail"), e);
            }
        }
    }

    /**
     * Handler for all messages we receive.
     *
     * @param datagram Message
     * @param client From what client?
     */
    public void parse(HashMap<String, Object> datagram, TCPClientHandler client) {

    }

    /**
     * Send a message!
     *
     * @param datagram Message to send
     * @param datAddress Host to send to (which computer?)
     * @param datPort Port to send to (which program on that computer?)
     */
    public void send(HashMap<String, Object> datagram, InetAddress datAddress, int datPort) {
        logger.trace(StringManager.fmt(JGameStr.getString("net.sendMSG"), serverSocket.getInetAddress(),
                serverSocket.getLocalPort(), datAddress, datPort, datagram));
        clients.get(datAddress + ":" + datPort).send(datagram);
    }

    /**
     * Close all connections. Calls {@link TCPClientHandler}.shutdown() for every client.
     *
     * @throws IOException Closing connections may fail.
     */
    public void shutdown() throws IOException {
        for (TCPClientHandler client : clients.values()) {
            try {
                client.shutdown();
            } catch (IOException e) {
                logger.warn(JGameStr.getString("net.shutdownFail"), e);
            }
        }
        serverSocket.close();
    }
}
