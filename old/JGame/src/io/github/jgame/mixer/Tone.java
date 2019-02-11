package io.github.jgame.mixer;

import java.util.LinkedList;

public class Tone {
    private boolean isStereo;
    public byte[] data;
    private SoundGenerator parent;

    private LinkedList<TonePlayer> threads = new LinkedList<>();

    public Tone(SoundGenerator player, byte[] tone, boolean stereo) {
        isStereo = stereo;
        data = tone;
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
        threads.add(new TonePlayer(parent, data, isStereo));
    }
}