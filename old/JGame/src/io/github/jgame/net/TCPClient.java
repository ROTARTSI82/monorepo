package io.github.jgame.net;

import io.github.jgame.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;

public class TCPClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private HashMap<String, Integer> serialTable;
    private HashMap<Integer, String> deserialTable;

    public TCPClient(String hostname, int portNum) throws Exception {
        socket = new Socket(hostname, portNum);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        serialTable = getActionTable();
        deserialTable = new HashMap<>();
        for (String action : serialTable.keySet()) {
            deserialTable.put(serialTable.get(action), action);
        }
    }

    public void send(HashMap<String, Object> datagram) {
        String send = Base64.getEncoder().encodeToString(NetUtils.serialize(datagram, serialTable));
        out.println(send);
    }

    public HashMap<String, Integer> getActionTable() {
        return Constants.BUILTIN_ACTIONS;
    }

    public void update() throws Exception {
        HashMap<String, Object> dat = NetUtils.deserialize(Base64.getDecoder().decode(in.readLine()),
                deserialTable);
        if (dat == null) {
            return;
        }
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

    public void onKick(String reason) throws Exception {
        shutdown();
    }

    public void onServerShutdown() {

    }

    public void parse(HashMap<String, Object> datagram) {

    }

    public void shutdown() throws Exception {
        HashMap<String, Object> leaveMsg = new HashMap<>();
        leaveMsg.put("action", "clientShutdown");
        send(leaveMsg);

        socket.close();
        in.close();
        out.close();
    }
}
