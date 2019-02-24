package io.github.jgame.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;

public class TCPClientHandler {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private TCPServer factory;

    public TCPClientHandler(Socket clientSocket, TCPServer parent) throws Exception {
        socket = clientSocket;
        factory = parent;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


    }

    public void send(HashMap<String, Object> datagram) {
        String send = Base64.getEncoder().encodeToString(NetUtils.serialize(datagram, factory.serialTable));
        out.println(send);
    }

    public void update() throws Exception {
        HashMap<String, Object> dat = NetUtils.deserialize(Base64.getDecoder().decode(in.readLine()),
                factory.deserialTable);
        if (dat == null) {
            return;
        }
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

    public void onClientShutdown() {
        factory.removeClient(this);
    }

    @Override
    public String toString() {
        return socket.getInetAddress() + ":" + socket.getPort();
    }

    public void parse(HashMap<String, Object> datagram) {

    }

    public void shutdown() throws Exception {
        HashMap<String, Object> leaveMsg = new HashMap<>();
        leaveMsg.put("action", "serverShutdown");
        send(leaveMsg);

        in.close();
        out.close();
        socket.close();
    }
}
