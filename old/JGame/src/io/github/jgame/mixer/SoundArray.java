package io.github.jgame.mixer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class SoundArray {
    private float rate;
    private int size;

    public SoundArray(float sampleRate, int sampleSize) {
        // Set signed to true if sampleSize is negative, and use absolute value.
        rate = sampleRate;
        size = sampleSize;
    }

    public static double[] combine(double[] a, double[] b) {
        double[] c = new double[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static double[][] combine(double[][] a, double[][] b) {
        double[][] c = new double[a.length + b.length][2];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static void main(String[] args) {
        SoundArray arr = new SoundArray(44100, -16);
        try {
            Note[] notes = new Note[]{
                    new Note("E4", 0.5, 0.25),
                    new Note("D4", 0.5, 0.25),
                    new Note("C4", 0.5, 0.5),
                    new Note("E4", 0.5, 0.25),
                    new Note("D4", 0.5, 0.25),
                    new Note("C4", 0.5, 0.5),
                    new Note("C4", 0.5, 0.25),
                    new Note("C4", 0.5, 0.25),
                    new Note("C4", 0.5, 0.25),
                    new Note("C4", 0.5, 0.5),
                    new Note("D4", 0.5, 0.25),
                    new Note("D4", 0.5, 0.25),
                    new Note("D4", 0.5, 0.25),
                    new Note("D4", 0.5, 0.25),
                    new Note("E4", 0.5, 0.25),
                    new Note("D4", 0.5, 0.25),
                    new Note("C4", 0.5, 0.5)
            };
            Tone song = new Tone(arr, arr.fromNoteArray(notes), 100);
            song.play();
            while (song.isPlaying()) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double[] fromNoteArray(Note[] notes) {
        double[] ret = new double[0];
        for (Note note : notes) {
            double[] rest = new double[(int) (rate * note.restTime)];
            double[] tone = getMono(note.frequency, note.holdTime);
            ret = combine(ret, combine(tone, rest));
        }
        return ret;
    }

    public void playMono(double[] tones, float volume) throws Exception {
        byte[] buf = new byte[2];
        AudioFormat af = new AudioFormat(rate, Math.abs(size),
                1, size < 0, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();

        int i = 0;
        for (double tone : tones) {
            double angle = i / (rate / tone) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * volume);  // volume=100
            buf[1] = buf[0];

            sdl.write(buf, 0, 2);
            i++;
        }

        sdl.drain();
        sdl.stop();
    }

    public void playStereo(double[][] tones, float volume) throws Exception {
        byte[] buf = new byte[4];  // {L, L, R, R}

        AudioFormat af = new AudioFormat(rate, Math.abs(size),
                2, size < 0, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();

        int i = 0;
        for (double[] lr : tones) {
            double left = i / (rate / lr[0]) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(left) * volume);
            buf[1] = buf[0];

            double right = i / (rate / lr[1]) * 2.0 * Math.PI;
            buf[2] = (byte) (Math.sin(right) * volume);
            buf[3] = buf[2];

            sdl.write(buf, 0, 4);
            i++;
        }

        sdl.drain();
        sdl.stop();
    }

    public double[][] getStereo(float left, float right, float length) {
        double[][] buf = new double[(int) (rate * length)][2];
        for (int i = 0; i < rate * length; i++) {
            buf[i][0] = left;
            buf[i][1] = right;
        }
        return buf;
    }

    public double[] getMono(double tone, double length) {
        double[] buf = new double[(int) (rate * length)];
        for (int i = 0; i < rate * length; i++) {
            buf[i] = tone;
        }
        return buf;
    }
}
