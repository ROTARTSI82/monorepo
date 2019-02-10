package io.github.jgame.tests;

import io.github.jgame.mixer.Note;
import io.github.jgame.mixer.SoundArray;
import io.github.jgame.mixer.Tone;

public class SoundTest {
    public static void main(String[] args) {
        SoundArray arr = new SoundArray(44100, -16);
        try {
            Note[][] notes = new Note[][]{{new Note("C4", 0.25, 0.5, 125),
                    new Note("E4", 0.5, 0.25, 100)},
                    {new Note("D4", 0.25, 0.5, 125),
                            new Note("G4", 0.75, 0, 100)}};
            Tone song = new Tone(arr, arr.fromNoteArray(notes), false);
            song.play();
            while (song.isPlaying()) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
