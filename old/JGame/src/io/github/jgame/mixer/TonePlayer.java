package io.github.jgame.mixer;

import io.github.jgame.logging.GenericLogger;

import javax.sound.sampled.LineUnavailableException;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

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
            } catch (LineUnavailableException e) {
                logger.warning(fmt(JGameStr.getString("mixer.TonePlayer.playFail"),
                        GenericLogger.getStackTrace(e)));
            }
        }
    }
}
