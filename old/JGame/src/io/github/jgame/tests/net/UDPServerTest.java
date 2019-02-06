package io.github.jgame.tests.net;

import io.github.jgame.net.UDPServer;

import java.net.DatagramPacket;
import java.util.HashMap;

public class UDPServerTest extends UDPServer {
    static long pingNum = 0;

    public UDPServerTest(String host, int port) throws Exception {
        super(host, port);
    }

    public static void main(String[] args) {
        UDPServerTest server;
        try {
            server = new UDPServerTest("127.0.0.1", 3000);
            while (true) {
                try {
                    server.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {
        pingNum++;
        System.out.println(String.format("My num: %s Cli num: %s", pingNum,
                datagram.get("iternum")));
        try {
            HashMap<String, Object> toSend = new HashMap<>();
            toSend.put("iternum", pingNum);
            addVerifyPacket(toSend, 100, packet.getAddress(), packet.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
