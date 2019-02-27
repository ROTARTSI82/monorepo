package io.github.jgame.mixer;

import io.github.jgame.logging.GenericLogger;

import java.util.logging.Logger;

/**
 * TODO: add javadoc
 */
public class TonePlayer extends Thread {

    private Logger logger;
    private boolean isStereo;
    private SoundGenerator soundPlayer;
    private byte[] tone;

    TonePlayer(SoundGenerator player, byte[] data, boolean stereo) {
        logger = Logger.getLogger(this.getClass().getName());
        isStereo = stereo;
        soundPlayer = player;
        tone = data;
        this.setDaemon(true);  // Program can exit before we finish
        this.start();
    }

    @Override
    public void run() {
        synchronized (TonePlayer.class) {
            try {
                soundPlayer.play(tone, isStereo);
            } catch (Exception e) {
                logger.warning(String.format("Failed to play tone:\n%s",
                        GenericLogger.getStackTrace(e)));
            }
        }
    }
}
