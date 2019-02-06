package io.github.jgame.tests.net;

import io.github.jgame.net.UDPClient;

import java.net.DatagramPacket;
import java.util.HashMap;

public class UDPClientTest extends UDPClient {
    static long pingNum = 0;

    public UDPClientTest(String host, int port) throws Exception {
        super(host, port);
    }

    public static void main(String[] args) {
        try {
            UDPClientTest client = new UDPClientTest("127.0.0.1", 3000);
            HashMap<String, Object> toSend = new HashMap<>();
            toSend.put("iternum", pingNum);
            client.addVerifyPacket(toSend, 100);
            while (true) {
                try {
                    client.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(HashMap<String, Object> datagram, DatagramPacket p) {
        pingNum++;
        System.out.println(String.format("My num: %s Serv num: %s", pingNum,
                datagram.get("iternum")));
        try {
            HashMap<String, Object> toSend = new HashMap<>();
            toSend.put("iternum", pingNum);
            addVerifyPacket(toSend, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
