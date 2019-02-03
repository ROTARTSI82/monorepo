package io.github.jgame.mixer;

import io.github.jgame.logging.GenericLogger;

import javax.sound.sampled.*;
import java.io.IOException;
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

    public void reset() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        sound = AudioSystem.getClip();
        sound.open(audioIn);
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
