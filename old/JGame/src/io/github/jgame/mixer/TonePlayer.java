package io.github.jgame.mixer;

import javax.sound.sampled.LineUnavailableException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;

/**
 * Tone playing asynchronously
 */
public class TonePlayer extends Thread {

    private Logger logger;
    private boolean isStereo;
    private SoundGenerator soundPlayer;
    private byte[] tone;

    /**
     * Tone player for tones!
     * Plays tone by default when the constructor is called.
     *
     * @param player soundGenerator to play using
     * @param data   sound to play
     * @param stereo is stereo?
     */
    TonePlayer(SoundGenerator player, byte[] data, boolean stereo) {
        logger = Logger.getLogger(this.getClass().getName());
        isStereo = stereo;
        soundPlayer = player;
        tone = data;
        this.setDaemon(true);  // Program can exit before we finish
        this.start();
    }

    /**
     * Play the sound!
     */
    @Override
    public void run() {
        synchronized (TonePlayer.class) {
            try {
                soundPlayer.play(tone, isStereo);
            } catch (LineUnavailableException e) {
                logger.log(Level.WARNING, JGameStr.getString("mixer.TonePlayer.playFail"), e);
            }
        }
    }
}
