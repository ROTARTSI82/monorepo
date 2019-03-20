package io.github.jgame.mixer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Procedurally generate, play, and save sounds.
 */
public class SoundGenerator {
    public float rate;
    private int size;

    /**
     * Procedural generation!!!
     *
     * @param sampleRate Sample rate (per second)
     * @param sampleSize Sample size (default -16)
     */
    public SoundGenerator(float sampleRate, int sampleSize) {
        rate = sampleRate;
        size = sampleSize;
    }

    /**
     * Initiate the sound generator with default values (rate=44100, size=-16)
     */
    public SoundGenerator() {
        rate = 44100;
        size = -16;
    }

    /**
     * Combine two byte arrays. For joining two samples of sound together.
     *
     * @param a sound a
     * @param b sound b
     * @return joined together (a + b)
     */
    public static byte[] combine(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * Generate a byte array ready to be played (mono) from a 1d array of notes.
     * Play with (isStereo=false)
     *
     * @param notes Notes
     * @return byte array ready to play
     */
    public byte[] fromNoteArray(Note[] notes) {
        byte[] ret = new byte[0];
        for (Note note : notes) {
            byte[] rest = new byte[(int) (rate * note.restTime * 2)];
            byte[] tone = getMono(note.frequency, note.holdTime, note.noteVolume);
            ret = combine(ret, combine(tone, rest));
        }
        return ret;
    }

    /**
     * Generate a stereo sound from a 2d note array.
     * Note array is in the form of [[NOTE TO PLAY ON THE LEFT], [NOTES TO PLAY ON THE RIGHT]]
     * and not [[NOTE LEFT, NOTE RIGHT], [NOTE LEFT, NOTE RIGHT], ...]
     * <p>
     * Play using (isStereo=true)
     *
     * @param notes 2d array of notes to play
     * @return processed bytes ready to play
     */
    public byte[] fromNoteArray(Note[][] notes) {
        byte[] left = new byte[0];  // {L, L, R, R}
        byte[] right = new byte[0];
        byte[] buf;
        byte[] ret = new byte[]{};
        for (Note leftNote : notes[0]) {
            buf = getMono(leftNote.frequency, leftNote.holdTime, leftNote.noteVolume);
            left = combine(left, combine(buf, new byte[(int) (rate * leftNote.restTime * 2)]));
        }
        for (Note rightNote : notes[1]) {
            buf = getMono(rightNote.frequency, rightNote.holdTime, rightNote.noteVolume);
            right = combine(right, combine(buf, new byte[(int) (rate * rightNote.restTime * 2)]));
        }

        buf = new byte[4];
        int index = 0;
        while (ret.length < right.length + left.length) {
            try {
                buf[0] = left[index];
                buf[1] = left[index + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                buf[0] = 0;
                buf[1] = 0;
            }
            try {
                buf[2] = right[index];
                buf[3] = right[index + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                buf[2] = 0;
                buf[3] = 0;
            }
            ret = combine(ret, buf);
            index += 2;
        }
        return ret;
    }

    /**
     * Parse a 1d array of frequencies to play at {@link #rate} hertz.
     * Play using (isStereo=false)
     *
     * @param tones  Tones
     * @param volume volume (multiplier)
     * @return processed bytes to play
     */
    public byte[] parse(double[] tones, float volume) {
        byte[] buf = new byte[2];
        byte[] ret = new byte[]{};

        int i = 0;
        for (double tone : tones) {
            double angle = i / (rate / tone) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * volume);  // volume=100
            buf[1] = buf[0];

            ret = combine(ret, buf);
            i++;
        }
        return ret;
    }

    /**
     * Parse a 2d array of frequencies to play at {@link #rate} hertz.
     * Play using (isStereo=true)
     *
     * @param tones Tones
     * @param volume volume (multiplier)
     * @return processed bytes to play
     */
    public byte[] parse(double[][] tones, float volume) {
        byte[] buf = new byte[4];  // {L, L, R, R}
        byte[] ret = new byte[]{};

        int i = 0;
        for (double[] lr : tones) {
            double left = i / (rate / lr[0]) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(left) * volume);
            buf[1] = buf[0];

            double right = i / (rate / lr[1]) * 2.0 * Math.PI;
            buf[2] = (byte) (Math.sin(right) * volume);
            buf[3] = buf[2];
            ret = combine(ret, buf);
            i++;
        }
        return ret;
    }

    /**
     * Plays the tone in the buffer. This function blocks until the tone is done playing.
     *
     * @param buf Buffer to play
     * @param isStereo is stereo audio?
     * @throws LineUnavailableException SourceDataLine cannot be created.
     */
    public void play(byte[] buf, boolean isStereo) throws LineUnavailableException {
        AudioFormat af = new AudioFormat(rate, Math.abs(size),
                isStereo ? 2 : 1, size < 0, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();
        sdl.write(buf, 0, buf.length);
        sdl.drain();
        sdl.stop();
    }

    /**
     * Save the byte data to a file using a DataOutputStream.
     * This can be loaded later and played.
     *
     * @param data sound data
     * @param file file to save to
     */
    public void save(byte[] data, File file) {
        try {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(file));
            outStream.write(data);
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load sound data from a file.
     *
     * @param file file to load
     * @param stereo isStereo audio?
     * @return Tone to play
     * @throws IOException Invalid file
     */
    public Tone load(File file, boolean stereo) throws IOException {
        byte[] buf = Files.readAllBytes(file.toPath());
        return new Tone(this, buf, stereo);
    }

    /**
     * Get the stereo audio for the following frequencies on the left and right for length seconds.
     *
     * @param left Left freq
     * @param right right freq
     * @param length Length in seconds to play for
     * @param volume Volume (multiplier)
     * @return processed bytes to play
     */
    public byte[] getStereo(double left, double right, double length, float volume) {
        double[][] buf = new double[(int) (rate * length)][2];
        for (int i = 0; i < (long) (rate * length); i++) {
            buf[i][0] = left;
            buf[i][1] = right;
        }
        return parse(buf, volume);
    }

    /**
     * Get the mono audio for the following frequencies for length seconds.
     *
     * @param tone frequency
     * @param length length in seconds
     * @param volume volume (multiplier)
     * @return processed bytes to play
     */
    public byte[] getMono(double tone, double length, float volume) {
        double[] buf = new double[(int) (rate * length)];
        for (int i = 0; i < (long) (rate * length); i++) {
            buf[i] = tone;
        }
        return parse(buf, volume);
    }
}
