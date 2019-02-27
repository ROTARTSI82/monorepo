package io.github.jgame.mixer;

import io.github.jgame.logging.GenericLogger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Sound object. Can be loaded from files or URLs
 * See {@link Clip}
 */
public class Sound {
    public String name;
    private Clip sound;
    private URL url;
    private Logger logger;

    /**
     * Load sound from file
     *
     * @param filename File to load
     */
    public Sound(String filename) {
        name = filename;
        logger = Logger.getLogger(this.getClass().getName());
        url = this.getClass().getClassLoader().getResource(filename);

        try {
            assert url != null;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            sound = AudioSystem.getClip();
            sound.open(audioIn);
        } catch (Exception e) {
            logger.warning(String.format("Failed to load %s:\n%s", name, GenericLogger.getStackTrace(e)));
        }
    }

    /**
     * Load sound from url
     *
     * @param soundURL url to load
     */
    public Sound(URL soundURL) {
        url = soundURL;
        name = soundURL.toString();

        logger = Logger.getLogger(this.getClass().getName());
        try {
            assert url != null;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            sound = AudioSystem.getClip();
            sound.open(audioIn);
        } catch (Exception e) {
            logger.warning(String.format("Failed to load %s:\n%s", name, GenericLogger.getStackTrace(e)));
        }
    }

    /**
     * Reset sound and prep it for playing over itself. See {@link #play}
     */
    public void reset() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            sound = AudioSystem.getClip();
            sound.open(audioIn);
        } catch (Exception e) {
            logger.warning(String.format("Failed to reset %s:\n%s", name, GenericLogger.getStackTrace(e)));
        }
    }

    /**
     * Normalize the value of a FloatControl
     *
     * @param control FloatControl
     * @return Normalized value (between [0.0, 1.0])
     */
    private float getControlVal(FloatControl control) {
        float min = control.getMinimum();
        float max = control.getMaximum();
        return (control.getValue() - min) / (max - min);
    }

    /**
     * Is playing
     *
     * @return true if clip is active
     */
    public boolean isPlaying() {
        return sound.isActive();
    }

    /**
     * Get normalized value of sound FloatControl
     *
     * @return Normalized value (See {@link #getControlVal(FloatControl)}
     */
    public float getVolume() {
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        return getControlVal(volume);
    }

    /**
     * Set volume float control to normalized value. See {@link #scaleToRange(float, FloatControl)}
     *
     * @param newVolume normalized value
     */
    public void setVolume(float newVolume) {
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(scaleToRange(newVolume, volume));
    }

    /**
     * Scale a normalized value (0 - 1) to the range of a FloatControl
     *
     * @param x       normalized value
     * @param control FloatControl
     * @return scaled value to control.getMinimum() and control.getMaximum()
     */
    private float scaleToRange(float x, FloatControl control) {
        float min = control.getMinimum();
        float max = control.getMaximum();
        return x * (max - min) + min;
    }

    /**
     * Play sound.
     *
     * With resetting enabled, the sound is allowed to play over itself, but it uses more resources and threads.
     * <ul>
     *     <li>
     *         Instead of interrupting itself if play() is called before the sound is finished
     *     </li>
     * </ul>
     * @param reset Resetting enabled (calls {@link #reset})
     */
    public void play(boolean reset) {
        try {
            if (reset) {
                reset();
            }
            stop();
            skipTo(0);
            sound.start();
        } catch (Exception e) {
            logger.info(String.format("Failed to play %s:\n%s", name, GenericLogger.getStackTrace(e)));
        }
    }

    /**
     * Stop the sound. see {@code Clip.stop}
     */
    public void stop() {
        sound.stop();
    }

    /**
     * Skip to sound to the microseconds position. See {@code Clip.setMicrosecondPosition}
     *
     * @param micros Microsecond position
     */
    public void skipTo(long micros) {
        sound.setMicrosecondPosition(micros);
    }

    /**
     * Loop sound for {@code num} number of times. -1 will loop indefinitely (any negative number will, but use -1)
     * See {@code Clip.loop}
     * @param num number of loops
     */
    public void loopFor(int num) {
        sound.loop(num);
    }
}
