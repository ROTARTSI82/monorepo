package io.github.jgame.mixer;

import java.util.LinkedList;

public class Tone {
    private boolean isStereo;
    private byte[] data;
    private SoundArray parent;

    private LinkedList<TonePlayer> threads = new LinkedList<>();

    public Tone(SoundArray player, byte[] tone, boolean isMono) {
        isStereo = !isMono;
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
        if (isStereo) {
            threads.add(new TonePlayer(parent, data, isStereo));
        } else {
            threads.add(new TonePlayer(parent, data, isStereo));
        }
    }
}