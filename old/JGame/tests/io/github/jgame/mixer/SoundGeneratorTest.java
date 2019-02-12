package io.github.jgame.mixer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SoundGeneratorTest {
    private final int monoLengthMargin = 2;
    private final int stereoLengthMargin = 2;
    private final int stereoPlaylenMargin = 125;
    private final int monoPlaylenMargin = 375;
    private SoundGenerator gen;

    @BeforeMethod
    public void setUp() {
        gen = new SoundGenerator();
    }

    @Test
    public void testCombine() {
        assertEquals(SoundGenerator.combine(new byte[]{0, 1}, new byte[]{2, 3}), new byte[]{0, 1, 2, 3});
    }

    @Test
    public void testFromNoteArray() {
        byte[] raw = gen.fromNoteArray(new Note[]{
                new Note("C4", 0.125, 0.25, 100),
                new Note("D4", 0.25, 0, 128)
        });
        long expectedLen = (long) (gen.rate * 1.25);
        System.out.println(String.format("Expected %s and got %s for mono", expectedLen, raw.length));
        assertTrue(expectedLen - monoLengthMargin <= raw.length &&
                expectedLen + monoLengthMargin >= raw.length);

        Tone tone = new Tone(gen, raw, false);
        tone.play();

        long start = System.currentTimeMillis();
        while (tone.isPlaying()) {
            assertTrue((System.currentTimeMillis() - start) <= (625 + monoPlaylenMargin));
        }
        assertTrue((System.currentTimeMillis() - start) >= 625);
    }

    @Test
    public void testFromNoteArray1() {
        byte[] raw = gen.fromNoteArray(new Note[][]{
                {
                        new Note("C4", 0.125, 0.25, 100),
                        new Note("D4", 0.25, 0, 128)
                },
                {
                        new Note("E4", 0.25, 0.125, 128),
                        new Note("F4", 0.125, 0.125, 100)
                }
        });
        long expectedLen = (long) (gen.rate * 2.5);
        System.out.println(String.format("Expected %s and got %s for stereo", expectedLen, raw.length));
        assertTrue(expectedLen - stereoLengthMargin <= raw.length &&
                expectedLen + stereoLengthMargin >= raw.length);

        Tone tone = new Tone(gen, raw, true);
        tone.play();

        long start = System.currentTimeMillis();
        while (tone.isPlaying()) {
            assertTrue((System.currentTimeMillis() - start) <= (625 + stereoPlaylenMargin));
        }
        assertTrue((System.currentTimeMillis() - start) >= 625);
    }
}