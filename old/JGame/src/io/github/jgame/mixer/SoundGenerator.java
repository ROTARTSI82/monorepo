package io.github.jgame.mixer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class SoundGenerator {
    public float rate;
    private int size;

    public SoundGenerator(float sampleRate, int sampleSize) {
        rate = sampleRate;
        size = sampleSize;
    }

    public SoundGenerator() {
        rate = 44100;
        size = -16;
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
            byte[] rest = new byte[(int) (rate * note.restTime * 2)];
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

    public void play(byte[] buf, boolean isStereo) throws Exception {
        AudioFormat af = new AudioFormat(rate, Math.abs(size),
                isStereo ? 2 : 1, size < 0, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();
        sdl.write(buf, 0, buf.length);
        sdl.drain();
        sdl.stop();
    }

    public void save(byte[] data, String file) {
        try {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(new File(file)));
            outStream.write(data);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Tone load(String file, boolean stereo) throws Exception {
        File inFile = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource(file)).toURI());
        byte[] buf = Files.readAllBytes(inFile.toPath());
        if (stereo) {
            byte[] tone = new byte[buf.length - 1];
            System.arraycopy(buf, 1, tone, 0, buf.length - 1);
            return new Tone(this, tone, true);
        }
        return new Tone(this, buf, false);
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
