package io.github.jgame.mixer;

import javax.sound.sampled.LineUnavailableException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;

/**
 * Tone playing asynchronously
 */
public class TonePlayer extends Thread {

    /**
     * Internal logger object used for logging events.
     */
    private Logger logger;

    /**
     * Should the {@link #tone} be interpreted as stereo or mono sound?
     * <p>
     * Stereo sound is in the format of: [L1, L1, R1, R1, L2, L2, R2, R2, ...]
     * Mono sound is: [T1, T1, T2, T2, T3, T3, ...]
     */
    private boolean isStereo;

    /**
     * Sound generator to use for playing the tone.
     */
    private SoundGenerator soundPlayer;

    /**
     * The raw sound data.
     */
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
     * Play the sound! (Async, of course)
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
