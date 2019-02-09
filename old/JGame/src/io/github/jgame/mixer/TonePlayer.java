package io.github.jgame.mixer;

import io.github.jgame.logging.GenericLogger;

import java.util.logging.Logger;

public class TonePlayer extends Thread {

    private Logger logger;
    private boolean isStereo;
    private SoundArray soundPlayer;
    private double[] loadedMono;
    private double[][] loadedStereo;
    private float playVolume;

    TonePlayer(SoundArray player, double[][] stereoSound, float volume) {
        logger = Logger.getLogger(this.getClass().getName());
        isStereo = true;
        soundPlayer = player;
        loadedStereo = stereoSound;
        playVolume = volume;
        this.setDaemon(true);  // Program can exit before we finish
        this.run();
    }

    TonePlayer(SoundArray player, double[] monoSound, float volume) {
        logger = Logger.getLogger(this.getClass().getName());
        isStereo = false;
        soundPlayer = player;
        loadedMono = monoSound;
        playVolume = volume;
        this.setDaemon(true);  // Program can exit before we finish
        this.run();
    }

    @Override
    public void run() {
        synchronized (TonePlayer.class) {
            try {
                if (isStereo) {
                    soundPlayer.playStereo(loadedStereo, playVolume);
                } else {
                    soundPlayer.playMono(loadedMono, playVolume);
                }
            } catch (Exception e) {
                logger.warning(String.format("Failed to play tone:\n%s",
                        GenericLogger.getStackTrace(e)));
            }
        }
    }
}
