package io.github.jgame.mixer;

import java.util.LinkedList;

public class Tone {
    private boolean isStereo;
    private double[] monoTone;
    private double[][] stereoTone;
    private float playVolume;
    private SoundArray parent;

    private LinkedList<TonePlayer> threads = new LinkedList<>();

    public Tone(SoundArray player, double[] tone, float volume) {
        isStereo = false;
        playVolume = volume;
        monoTone = tone;
        parent = player;
    }

    public Tone(SoundArray player, double[][] tone, float volume) {
        isStereo = true;
        playVolume = volume;
        stereoTone = tone;
        parent = player;
    }

    public boolean isPlaying() {
        boolean ret = false;
        LinkedList<TonePlayer> newThreads = new LinkedList<>();
        for (TonePlayer thread : threads) {
            if (thread.isAlive()) {
                newThreads.add(thread);
                ret = true;
            }
        }
        threads = newThreads;
        return ret;
    }

    public void play() {
        if (isStereo) {
            threads.add(new TonePlayer(parent, stereoTone, playVolume));
        } else {
            threads.add(new TonePlayer(parent, monoTone, playVolume));
        }
    }
}