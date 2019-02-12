package io.github.jgame.tests;

import io.github.jgame.mixer.Note;
import io.github.jgame.mixer.SoundGenerator;
import io.github.jgame.mixer.Tone;

import java.util.Arrays;

public class SoundTest {
    public static void main(String[] args) {
        SoundGenerator arr = new SoundGenerator(44100, -16);
        try {
            Note[][] notes = new Note[][]{{new Note("C4", 0.25, 0.5, 64),
                    new Note("E4", 0.5, 0.25, 128)},
                    {new Note("D4", 0.25, 0.5, 128),
                            new Note("G4", 0.75, 0, 64)}};

            System.out.println("Building..");

            long start = System.currentTimeMillis();
            Tone song = arr.load("test.wav.bin", true);
            //Tone song = new Tone(arr, arr.fromNoteArray(notes), true);
            //arr.save(song.data, "resources/test.wav.bin");
            System.out.println(Arrays.toString(song.data));
            System.out.println("Compiled " + (song.data.length / arr.rate / 2) * 1000 + "ms of audio in "
                    + (System.currentTimeMillis() - start) + "ms. Playing");
            song.play();
            start = System.currentTimeMillis();
            while (song.isPlaying()) {
            }
            System.out.println("took " + (System.currentTimeMillis() - start) + "ms to play");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
