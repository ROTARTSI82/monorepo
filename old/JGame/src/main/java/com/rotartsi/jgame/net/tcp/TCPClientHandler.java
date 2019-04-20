package com.rotartsi.jgame.net.tcp;

import com.rotartsi.jgame.net.NetUtils;
import com.rotartsi.jgame.util.StringManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;

import static com.rotartsi.jgame.Constants.JGameStr;

/**
 * Representation of clients used by the {@link TCPServer}
 */
public class TCPClientHandler {
    /**
     * Output stream. Write to this stream to send data to the client.
     */
    private PrintWriter out;

    /**
     * Input stream. Read this stream to read messages from the client.
     */
    private BufferedReader in;

    /**
     * Socket. Contains host and port of the client. See {@link Socket}
     */
    private Socket socket;

    /**
     * The server this client is connected to.
     */
    private TCPServer factory;

    /**
     * Internal logger object used for logging.
     */
    private Logger logger = LogManager.getLogger(TCPClientHandler.class);

    /**
     * New client!
     *
     * @param clientSocket Address the client is at
     * @param parent       TCPServer they connected to
     * @throws IOException Opening the connection may fail
     */
    public TCPClientHandler(Socket clientSocket, TCPServer parent) throws IOException {
        socket = clientSocket;
        factory = parent;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /**
     * Send a message!
     *
     * @param datagram Message
     */
    public void send(HashMap<String, Object> datagram) {
        String send = Base64.getEncoder().encodeToString(NetUtils.serialize(datagram, factory.serialTable));
        out.println(send);
    }

    /**
     * Handle clientShutdown from our client.
     *
     * @throws IOException Reading the input may fail.
     */
    public void update() throws IOException {
        HashMap<String, Object> dat = NetUtils.deserialize(Base64.getDecoder().decode(in.readLine()),
                factory.deserialTable);
        if (dat == null) {
            return;
        }
        logger.trace(StringManager.fmt(JGameStr.getString("net.recvMSG"), factory.serverSocket.getInetAddress(),
                factory.serverSocket.getLocalPort(), socket.getInetAddress(), socket.getPort(), dat));
        String action = (String) dat.get("action");
        if (action != null) {
            if (action.equals("clientShutdown")) {
                onClientShutdown();
                return;
            }
        }
        factory.parse(dat, this);
        parse(dat);
    }

    /**
     * Handler for clientShutdown events. Remove the client from the {@link TCPServer}'s list of clients.
     */
    public void onClientShutdown() {
        factory.removeClient(this);
    }

    /**
     * toString() used by {@link TCPServer} to get a id for each {@link TCPClientHandler}.
     *
     * @return Since the socket is always unique, return port:host
     */
    @Override
    public String toString() {
        return socket.getInetAddress() + ":" + socket.getPort();
    }

    /**
     * Handler for all messages sent by the client
     *
     * @param datagram Messages
     */
    public void parse(HashMap<String, Object> datagram) {

    }

    /**
     * Close all connections
     *
     * @throws IOException May fail trying to close connections.
     */
    public void shutdown() throws IOException {
        HashMap<String, Object> leaveMsg = new HashMap<>();
        leaveMsg.put("action", "serverShutdown");
        send(leaveMsg);

        in.close();
        out.close();
        socket.close();
    }
}
