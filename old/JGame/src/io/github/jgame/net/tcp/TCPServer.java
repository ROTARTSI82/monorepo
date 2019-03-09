package io.github.jgame.net.tcp;

import io.github.jgame.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

public class TCPServer {
    private ServerSocket serverSocket;
    private HashMap<String, TCPClientHandler> clients = new HashMap<>();

    InetAddress host;
    int port;

    private int clientLimit;
    private Logger logger;

    public HashMap<String, Integer> serialTable;
    public HashMap<Integer, String> deserialTable;

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
        logger = Logger.getLogger(this.getClass().getName());
        host = InetAddress.getByName(hostname);
        port = portNum;
        serverSocket = new ServerSocket(port, 50, host);

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

    public void sendToAll(HashMap<String, Object> datagram) {
        for (TCPClientHandler handler : clients.values()) {
            handler.send(datagram);
        }
    }

    public void acceptNewClients() throws IOException {
        Socket client = serverSocket.accept();
        String key = fmt("%s:%s", client.getInetAddress().toString(), client.getPort());
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
            logger.info(fmt(JGameStr.getString("net.newClient"), host, port, key));
        } else {
            logger.warning(JGameStr.getString("net.TCPServer.alreadyExists"));
        }
    }

    public HashMap<String, Integer> getActionTable() {
        return Constants.BUILTIN_ACTIONS;
    }

    public void removeClient(TCPClientHandler client) {
        clients.remove(client.toString());
    }

    public void update() {
        for (TCPClientHandler client : clients.values()) {
            try {
                client.update();
            } catch (IOException e) {
                logger.log(Level.WARNING, JGameStr.getString("net.TCPServer.updateFail"), e);
            }
        }
    }

    public void parse(HashMap<String, Object> datagram, TCPClientHandler client) {

    }

    public void send(HashMap<String, Object> datagram, InetAddress datAddress, int datPort) {
        logger.finest(fmt(JGameStr.getString("net.sendMSG"), host, port, datAddress, datPort, datagram));
        clients.get(datAddress + ":" + datPort).send(datagram);
    }

    public void shutdown() throws IOException {
        for (TCPClientHandler client : clients.values()) {
            try {
                client.shutdown();
            } catch (IOException e) {
                logger.log(Level.WARNING, JGameStr.getString("net.shutdownFail"), e);
            }
        }
        serverSocket.close();
    }
}
