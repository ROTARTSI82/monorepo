package io.github.jgame.mixer;

import io.github.jgame.logging.GenericLogger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class Sound {
    public String name;
    private Clip sound;
    private URL url;
    private Logger logger;

    public Sound(String filename, LoadModes loadMode) {
        name = filename;
        logger = Logger.getLogger(this.getClass().getName());
        switch (loadMode) {
            case FROM_FILE: {
                url = this.getClass().getClassLoader().getResource(filename);
                break;
            }
            case FROM_URL: {
                try {
                    url = new URL(filename);
                } catch (MalformedURLException e) {
                    logger.warning(String.format("Malformed URL: %s\n%s", filename, GenericLogger.getStackTrace(e)));
                }
                break;
            }
        }
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            sound = AudioSystem.getClip();
            sound.open(audioIn);
        } catch (Exception e) {
            logger.warning(String.format("Failed to load %s:\n%s", name, GenericLogger.getStackTrace(e)));
        }
    }

    public void reset() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            sound = AudioSystem.getClip();
            sound.open(audioIn);
        } catch (Exception e) {
            logger.warning(String.format("Failed to reset %s:\n%s", name, GenericLogger.getStackTrace(e)));
        }
    }

    public float getVolume() {
        FloatControl gainControl = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }

    public void setVolume(float volume) {
        if (volume > 1) {
            logger.info(String.format("volume=%s passed to setVolume(). Using volume=1", volume));
            volume = 1;
        } else if (volume < 0) {
            logger.info(String.format("volume=%s passed to setVolume(). Using volume=0", volume));
            volume = 0;
        }
        FloatControl gainControl = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

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

    public void stop() {
        sound.stop();
    }

    public void skipTo(long micros) {
        sound.setMicrosecondPosition(micros);
    }

    public void loopFor(int num) {
        sound.loop(num);
    }

    public enum LoadModes {
        FROM_URL, FROM_FILE
    }
}
