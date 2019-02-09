package io.github.jgame.mixer;

public class Tone {
    private boolean isStereo;
    private double[] monoTone;
    private double[][] stereoTone;
    private float playVolume;
    private SoundArray parent;

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

    public void play() {
        if (isStereo) {
            new TonePlayer(parent, stereoTone, playVolume);
        } else {
            new TonePlayer(parent, monoTone, playVolume);
        }
    }
}