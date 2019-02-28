package io.github.jgame.net;

import io.github.jgame.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

public class TCPClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Logger logger;

    private HashMap<String, Integer> serialTable;
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

    public void send(HashMap<String, Object> datagram) {
        logger.finest(fmt(JGameStr.getString("net.sendMSG"), "?", "?",
                socket.getInetAddress(), socket.getPort(), datagram));
        String send = Base64.getEncoder().encodeToString(NetUtils.serialize(datagram, serialTable));
        out.println(send);
    }

    public HashMap<String, Integer> getActionTable() {
        return Constants.BUILTIN_ACTIONS;
    }

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

    public void onKick(String reason) throws IOException {
        shutdown();
    }

    public void onServerShutdown() {

    }

    public void parse(HashMap<String, Object> datagram) {

    }

    public void shutdown() throws IOException {
        HashMap<String, Object> leaveMsg = new HashMap<>();
        leaveMsg.put("action", "clientShutdown");
        send(leaveMsg);

        socket.close();
        in.close();
        out.close();
    }
}
