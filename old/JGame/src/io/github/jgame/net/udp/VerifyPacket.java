package io.github.jgame.net.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;
import static io.github.jgame.util.StringManager.fmt;

public class VerifyPacket {
    private static final HashMap<String, Object> filler = new HashMap<>();

    static {
        filler.put("action", "filler");
    }

    private final Timer timer;
    String id;
    private HashMap<String, Object> rawSend = new HashMap<>();
    private InetAddress myHost;
    private int myPort;
    private int backoff;
    private Logger logger;
    private volatile boolean verified = false;
    private volatile boolean hasSent = false;
    private volatile boolean terminate = false;

    public VerifyPacket(HashMap<String, Object> datagram, int frequency, double multiplier,
                        InetAddress host, int port, UDPServer parent) {
        logger = Logger.getLogger(this.getClass().getName());

        if (!datagram.containsKey("action")) {
            datagram.put("action", null);
        }

        rawSend.put("action", "verifySend");
        rawSend.put("data", datagram);

        id = UUID.randomUUID().toString();
        rawSend.put("id", id);
        myHost = host;
        myPort = port;

        backoff = frequency;
        timer = new Timer();
        timer.schedule(getServerTask(multiplier, parent), backoff);
    }

    public VerifyPacket(HashMap<String, Object> datagram, int frequency, double multiplier, UDPClient parent) {
        logger = Logger.getLogger(this.getClass().getName());
        if (!datagram.containsKey("action")) {
            datagram.put("action", null);
        }

        rawSend.put("action", "verifySend");
        rawSend.put("data", datagram);

        id = UUID.randomUUID().toString();
        rawSend.put("id", id);

        backoff = frequency;
        timer = new Timer();
        timer.schedule(getClientTask(multiplier, parent), frequency);
    }

    private TimerTask getServerTask(double multiplier, UDPServer parent) {
        return new TimerTask() {
            @Override
            public void run() {
                synchronized (VerifyPacket.class) {
                    if ((verified && hasSent) || terminate) {
                        stop();
                        this.cancel();
                        return;
                    } else {
                        try {
                            parent.send(rawSend, myHost, myPort);
                            hasSent = true;
                        } catch (IOException err) {
                            logger.log(Level.WARNING, fmt(JGameStr.getString("net.UDP.resendFail"), rawSend), err);
                        }
                    }

                    try {
                        parent.send(filler, myHost, myPort);
                    } catch (IOException e) {
                        logger.log(Level.WARNING, JGameStr.getString("net.UDP.fillerFail"), e);
                    }

                    backoff *= multiplier;
                    try {
                        timer.schedule(getServerTask(multiplier, parent), backoff);
                        logger.finest(fmt(JGameStr.getString("net.UDP.reschedule"), rawSend, backoff));
                    } catch (IllegalStateException e) {
                        logger.log(Level.WARNING, fmt(JGameStr.getString("net.UDP.illegalTimer"), rawSend), e);
                    }
                    this.cancel();
                }
            }
        };
    }

    private TimerTask getClientTask(double multiplier, UDPClient parent) {
        return new TimerTask() {
            @Override
            public void run() {
                synchronized (VerifyPacket.class) {
                    if ((verified && hasSent) || terminate) {
                        stop();
                        this.cancel();
                        return;
                    } else {
                        try {
                            parent.send(rawSend);
                            hasSent = true;
                        } catch (IOException err) {
                            logger.log(Level.WARNING, fmt(JGameStr.getString("net.UDP.resendFail"), rawSend), err);
                        }
                    }

                    try {
                        parent.send(filler);
                    } catch (IOException e) {
                        logger.log(Level.WARNING, JGameStr.getString("net.UDP.fillerFail"), e);
                    }

                    backoff *= multiplier;
                    try {
                        timer.schedule(getClientTask(multiplier, parent), backoff);
                        logger.finest(fmt(JGameStr.getString("net.UDP.reschedule"), rawSend, backoff));
                    } catch (IllegalStateException e) {
                        logger.log(Level.WARNING, fmt(JGameStr.getString("net.UDP.illegalTimer"), rawSend), e);
                    }
                    this.cancel();
                }
            }
        };
    }

    void onConfirm() {
        verified = true;
    }

    synchronized void stop() {
        logger.fine(fmt(JGameStr.getString("net.UDP.threadStop"), rawSend));
        timer.cancel();
        timer.purge();
        terminate = true;
    }
}
