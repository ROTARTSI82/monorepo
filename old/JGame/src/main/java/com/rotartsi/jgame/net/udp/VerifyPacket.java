package com.rotartsi.jgame.net.udp;

import com.rotartsi.jgame.util.StringManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;

/**
 * Implementation of basic TCP functionality in a sense that the datagram is sent again after a delay
 * if we do not hear back from the server. With exponential backoff.
 */
public class VerifyPacket {
    /**
     * The filler message to send. It flushes the stream and allows our actual message to appear.
     */
    private static final HashMap<String, Object> filler = new HashMap<>();

    static {
        filler.put("action", "filler");
    }

    /**
     * Async timer used to execute resends/checks
     */
    private final Timer timer;

    /**
     * The UUID used by this packet. It is accessed by the UDPServer. DO NOT DELETE.
     */
    String id;

    /**
     * The host that we are sending to.
     */
    private InetAddress myHost;

    /**
     * The port we are sending to.
     */
    private int myPort;
    /**
     * Raw HashMap we are attempting to send.
     */
    private HashMap<String, Object> rawSend = new HashMap<>();
    /**
     * The current delay between resends and checks.
     */
    private int backoff;

    /**
     * The internal logger object used for logging.
     */
    private Logger logger = Logger.getLogger(VerifyPacket.class);

    /**
     * Set this flag if the packet has been verified. This flag and {@link #hasSent} must be set for
     * the VerifyPacket to terminate.
     */
    private volatile boolean verified = false;

    /**
     * Set this flag if the packet has been sent. This flag and {@link #verified} must be set for
     * the VerifyPacket to terminate.
     */
    private volatile boolean hasSent = false;

    /**
     * Set this flag if the packet needs to be terminated. No matter what, the VerifyPacket would terminate
     * if this flag is set.
     */
    private volatile boolean terminate = false;

    /**
     * VerifyPacket for {@link UDPServer}
     *
     * @param datagram   Message to send
     * @param frequency  Starting delay before resend/check
     * @param multiplier exp backoff
     * @param host       Host to send to
     * @param port       Port to send to
     * @param parent     UDPServer to send with
     */
    public VerifyPacket(HashMap<String, Object> datagram, int frequency, double multiplier,
                        InetAddress host, int port, UDPServer parent) {

        if (!datagram.containsKey("action")) {
            datagram.put("action", null);
        }

        rawSend.put("action", "verifySend");
        rawSend.put("data", datagram);

        id = UUID.randomUUID().toString();
        rawSend.put("id", id);
        myHost = host;
        myPort = port;

        try {
            parent.send(rawSend, myHost, myPort);
            hasSent = true;
        } catch (IOException err) {
            logger.warn(fmt(JGameStr.getString("net.UDP.resendFail"), rawSend), err);
        }

        backoff = frequency;
        timer = new Timer();
        timer.schedule(getServerTask(multiplier, parent), backoff);
    }

    /**
     * VerifyPacket for {@link UDPClient}
     *
     * @param datagram Datagram to send
     * @param frequency Starting delay before resending/checking
     * @param multiplier exponential backoff
     * @param parent UDPClient to send with
     */
    public VerifyPacket(HashMap<String, Object> datagram, int frequency, double multiplier, UDPClient parent) {
        if (!datagram.containsKey("action")) {
            datagram.put("action", null);
        }

        rawSend.put("action", "verifySend");
        rawSend.put("data", datagram);

        id = UUID.randomUUID().toString();
        rawSend.put("id", id);

        try {
            parent.send(rawSend);
            hasSent = true;
        } catch (IOException err) {
            logger.warn(fmt(JGameStr.getString("net.UDP.resendFail"), rawSend), err);
        }

        backoff = frequency;
        timer = new Timer();
        timer.schedule(getClientTask(multiplier, parent), frequency);
    }

    /**
     * VerifyPacket for {@link UDPServer}
     *
     * @param multiplier Multiplier for exp backoff
     * @param parent UDPServer to send with
     * @return The TimerTask to execute. (TimerTask calls this method again with backoff added, so sorta recursive)
     */
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
                            logger.warn(fmt(JGameStr.getString("net.UDP.resendFail"), rawSend), err);
                        }
                    }

                    try {
                        parent.send(filler, myHost, myPort); // Send the filler to flush the stream so the other end gets our msg.
                    } catch (IOException e) {
                        logger.warn(JGameStr.getString("net.UDP.fillerFail"), e);
                    }

                    backoff *= multiplier;
                    try {
                        timer.schedule(getServerTask(multiplier, parent), backoff);
                        logger.trace(StringManager.fmt(JGameStr.getString("net.UDP.reschedule"), rawSend, backoff));
                    } catch (IllegalStateException e) {
                        logger.warn(fmt(JGameStr.getString("net.UDP.illegalTimer"), rawSend), e);
                    }
                    this.cancel();
                }
            }
        };
    }

    /**
     * VerifyPacket for {@link UDPClient}
     *
     * @param multiplier Exponential backoff
     * @param parent UDPClient to send with
     * @return TimerTask to execute (TimerTask calls this method again with backoff added, so sorta recursive)
     */
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
                            logger.warn(fmt(JGameStr.getString("net.UDP.resendFail"), rawSend), err);
                        }
                    }

                    try {
                        parent.send(filler);  // Send the filler to flush the stream so the other end gets our msg.
                    } catch (IOException e) {
                        logger.warn(JGameStr.getString("net.UDP.fillerFail"), e);
                    }

                    backoff *= multiplier;
                    try {
                        timer.schedule(getClientTask(multiplier, parent), backoff);
                        logger.trace(StringManager.fmt(JGameStr.getString("net.UDP.reschedule"), rawSend, backoff));
                    } catch (IllegalStateException e) {
                        logger.warn(fmt(JGameStr.getString("net.UDP.illegalTimer"), rawSend), e);
                    }
                    this.cancel();
                }
            }
        };
    }

    /**
     * Confirm the packet!
     */
    void onConfirm() {
        verified = true;
    }

    /**
     * Stop all TimerTasks and Timers.
     */
    synchronized void stop() {
        logger.trace(fmt(JGameStr.getString("net.UDP.threadStop"), rawSend));
        timer.cancel();
        timer.purge();
        terminate = true;
    }
}
