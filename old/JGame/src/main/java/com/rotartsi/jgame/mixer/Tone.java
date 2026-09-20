package com.rotartsi.jgame.mixer;

import java.util.LinkedList;

/**
 * Playable synthetic tone
 */
public class Tone {
    /**
     * The raw data to play
     */
    public byte[] data;

    /**
     * Should the raw data be interpreted as
     * stereo: [L1, L1, R1, R1, L2, L2, R1, R1, ...]
     * or mono: [T1, T1, T2, T2, T3, T3, ...]
     */
    private boolean isStereo;
    /**
     * SoundGenerator to use to play the tone.
     */
    private SoundGenerator parent;

    /**
     * List of active threads playing the sound. A new thread is created every time {@link #play} is called.
     */
    private LinkedList<TonePlayer> threads = new LinkedList<>();

    /**
     * Procedural generation!
     *
     * @param player SoundGenerator to use to play this tone (should be the one that generated the tone)
     * @param tone   byte array of the sound
     * @param stereo is stereo?
     */
    public Tone(SoundGenerator player, byte[] tone, boolean stereo) {
        isStereo = stereo;
        data = tone;
        parent = player;
    }

    /**
     * Is the sound active?
     *
     * @return True if sound is playing (duh)
     */
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

    /**
     * Play the sound asynchronously via {@link TonePlayer}
     */
    public void play() {
        threads.add(new TonePlayer(parent, data, isStereo));
    }
}