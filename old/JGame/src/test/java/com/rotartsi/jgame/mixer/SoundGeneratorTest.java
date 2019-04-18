package com.rotartsi.jgame.mixer;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SoundGeneratorTest {
    private final int monoPlaylenMargin = 750;
    private Tone mono;

    private final int monoLengthMargin = 2;
    private final int stereoLengthMargin = 2;
    private final int stereoPlaylenMargin = 125;
    private Tone stereo;
    private SoundGenerator gen;
    private Logger logger = Logger.getLogger(SoundGeneratorTest.class);

    @BeforeSuite
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
        logger.info(fmt(JGameStr.getString("mixer.SoundGeneratorTest.expectedMono"),
                expectedLen, raw.length));
        assertTrue(expectedLen - monoLengthMargin <= raw.length &&
                expectedLen + monoLengthMargin >= raw.length);

        mono = new Tone(gen, raw, false);
        mono.play();

        long start = System.currentTimeMillis();
        while (mono.isPlaying()) {
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
        logger.info(fmt(JGameStr.getString("mixer.SoundGeneratorTest.expectedStereo"),
                expectedLen, raw.length));
        assertTrue(expectedLen - stereoLengthMargin <= raw.length &&
                expectedLen + stereoLengthMargin >= raw.length);

        stereo = new Tone(gen, raw, true);
        stereo.play();

        long start = System.currentTimeMillis();
        while (stereo.isPlaying()) {
            assertTrue((System.currentTimeMillis() - start) <= (625 + stereoPlaylenMargin));
        }
        assertTrue((System.currentTimeMillis() - start) >= 625);
    }

    @Test(dependsOnMethods = {"testFromNoteArray"})
    public void testMonoSave() throws Exception {
        File tmp = File.createTempFile("mono", ".tmp");
        gen.save(mono.data, tmp);
        Tone recovered = gen.load(tmp, false);
        assertEquals(recovered.data, mono.data);

        assertTrue(tmp.delete());
    }

    @Test(dependsOnMethods = {"testFromNoteArray1"})
    public void testStereoSave() throws Exception {
        File tmp = File.createTempFile("stereo", ".tmp");
        gen.save(stereo.data, tmp);
        Tone recovered = gen.load(tmp, true);
        assertEquals(recovered.data, stereo.data);
        assertTrue(tmp.delete());
    }
}