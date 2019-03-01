package io.github.jgame.net;

import org.testng.annotations.Test;

import java.net.DatagramPacket;
import java.util.HashMap;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

public class UDPTest {
    @Test(timeOut = 2000)
    public void testUDP() throws Exception {
        Server testServ = new Server("127.0.0.1", 3000);
        Client testCli = new Client("127.0.0.1", 3000, System.nanoTime());
        while (!testCli.done) {
            Thread.sleep(25);
            testServ.update();
            testCli.update();
        }
        testCli.shutdown();
        testServ.shutdown();
    }

    private class Server extends UDPServer {
        Server(String host, int port) throws Exception {
            super(host, port, 1);
        }

        @Override
        public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {
            System.out.println(fmt(JGameStr.getString("net.Test.serverGot"), datagram));
            HashMap<String, Object> dat = new HashMap<>();
            dat.put("ack", datagram.get("id"));
            try {
                this.addVerifyPacket(dat, 100, packet.getAddress(), packet.getPort());
                //this.send(dat, packet.getAddress(), packet.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Client extends UDPClient {
        long id;
        boolean done = false;

        Client(String host, int port, long myID) throws Exception {
            super(host, port);
            id = myID;

            HashMap<String, Object> dat = new HashMap<>();
            dat.put("id", id);
            this.addVerifyPacket(dat, 100);
            //this.send(dat);
        }

        @Override
        public void parse(HashMap<String, Object> datagram, DatagramPacket packet) {
            System.out.println(fmt(JGameStr.getString("net.Test.clientGot"), datagram));
            done = ((long) datagram.get("ack")) == id;
        }
    }
}
