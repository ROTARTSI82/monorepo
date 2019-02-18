package io.github.jgame.net;

import org.testng.annotations.Test;

import java.util.HashMap;

public class TCPTest {
    @Test(timeOut = 2000)
    public void testTCP() throws Exception {
        TestServer server = new TestServer();
        TestClient client = new TestClient();
        while (!client.done) {
            server.acceptNewClients();
            server.update();
            client.update();
        }
        server.shutdown();
        client.shutdown();
    }

    static class TestServer extends TCPServer {
        public TestServer() throws Exception {
            super("127.0.0.1", 3000, 1);
        }

        @Override
        public void parse(HashMap<String, Object> datagram, TCPClientHandler client) {
            HashMap<String, Object> ack = new HashMap<>();
            ack.put("ack", datagram.get("id"));
            client.send(ack);
        }
    }

    static class TestClient extends TCPClient {
        long id;
        private boolean done = false;

        public TestClient() throws Exception {
            super("127.0.0.1", 3000);
            id = System.nanoTime();
            HashMap<String, Object> dat = new HashMap<>();
            dat.put("id", id);
            send(dat);
        }

        @Override
        public void parse(HashMap<String, Object> datagram) {
            if (((long) datagram.get("ack")) == id) {
                done = true;
            }
        }
    }
}
