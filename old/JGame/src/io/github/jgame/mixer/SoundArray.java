package io.github.jgame.mixer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class SoundArray {
    private float rate;
    private int size;

    public SoundArray(float sampleRate, int sampleSize) {
        rate = sampleRate;
        size = sampleSize;
    }

    public static byte[] combine(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public byte[] fromNoteArray(Note[] notes) {
        byte[] ret = new byte[0];
        for (Note note : notes) {
            byte[] rest = new byte[(int) (rate * note.restTime * 2)];  // 2 bytes per frame
            byte[] tone = getMono(note.frequency, note.holdTime, note.noteVolume);
            ret = combine(ret, combine(tone, rest));
        }
        return ret;
    }

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
            buf[0] = left[index];
            buf[1] = left[index + 1];
            buf[2] = right[index];
            buf[3] = right[index + 1];
            ret = combine(ret, buf);
            index += 2;
        }
        return ret;
    }

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

    public void save() {

    }

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

    public void play(byte[] buf, boolean isMono) throws Exception {
        AudioFormat af = new AudioFormat(rate, Math.abs(size),
                isMono ? 1 : 2, size < 0, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();
        sdl.write(buf, 0, buf.length);
        sdl.drain();
        sdl.stop();
    }

    public byte[] getStereo(double left, double right, double length, float volume) {
        double[][] buf = new double[(int) (rate * length)][2];
        for (int i = 0; i < rate * length; i++) {
            buf[i][0] = left;
            buf[i][1] = right;
        }
        return parse(buf, volume);
    }

    public byte[] getMono(double tone, double length, float volume) {
        double[] buf = new double[(int) (rate * length)];
        for (int i = 0; i < rate * length; i++) {
            buf[i] = tone;
        }
        return parse(buf, volume);
    }
}
