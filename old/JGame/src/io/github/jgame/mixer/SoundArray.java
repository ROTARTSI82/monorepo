package io.github.jgame.mixer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.util.Arrays;

public class SoundArray {
    private float rate;
    private int size;

    public SoundArray(float sampleRate, int sampleSize) {
        // Set signed to true if sampleSize is negative, and use absolute value.
        rate = sampleRate;
        size = sampleSize;
    }

    public static byte[] combine(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[][] combine(byte[][] a, byte[][] b) {
        byte[][] c = new byte[a.length + b.length][2];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static void main(String[] args) {
        byte[][] a = new byte[][]{{0, 127}, {64, 32}};
        byte[][] b = new byte[][]{{-128, 64}, {-16, -8}};
        System.out.print("{");
        for (byte[] coords : combine(a, b)) {
            System.out.print(Arrays.toString(coords));
            System.out.print(", ");
        }
        System.out.println("}");

        byte[] c = new byte[]{0, 64};
        byte[] d = new byte[]{-128, 127};
        System.out.println(Arrays.toString(combine(c, d)));

        SoundArray arr = new SoundArray(44100, -16);
        try {
            arr.playStereo(arr.getStereo(440, 340, 5), 100);
            Tone tone = new Tone(arr, arr.getMono(440, 5), 100);
            tone.play();
            Thread.sleep(5000);
            tone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public double[] getMono(float tone, float length) {
        double[] buf = new double[(int) (rate * length)];
        for (int i = 0; i < rate * length; i++) {
            buf[i] = tone;
        }
        return buf;
    }
}
