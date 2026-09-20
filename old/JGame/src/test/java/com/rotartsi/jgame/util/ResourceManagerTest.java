package com.rotartsi.jgame.util;

import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.mixer.SoundManager;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Also includes tests for {@link FileUtils}
 */
public class ResourceManagerTest {
    private File testDir = new File("testFromDir");

    @Test
    public void testTouchFileAndFolder() {
        assertTrue(FileUtils.touchFile("testFromDir/ab/zyx1.png"));
        assertTrue(FileUtils.touchFile("testFromDir/ab/zyx2.png"));

        assertTrue(FileUtils.touchFile("testFromDir/ab/ac/gh1.wav"));
        assertTrue(FileUtils.touchFile("testFromDir/ab/ac/gh2.wav"));

        assertTrue(FileUtils.touchFolder("testFromDir/notAFileEither.wav"));
        assertTrue(FileUtils.touchFolder("testFromDir/imNotAFile.png"));
    }

    @Test(dependsOnMethods = "testFromDir")
    public void testDeleteDir() throws IOException {
        assertTrue(FileUtils.deleteDir(testDir));
    }

    @Test(dependsOnMethods = "testTouchFileAndFolder")
    public void testFromDir() {
        ImageManager im = new ImageManager();
        SoundManager sm = new SoundManager();

        Set<String> expectedSounds = new HashSet<>() {{  // [ab.ac.gh]
            add("ab.ac.gh");
        }};
        Set<String> expectedImages = new HashSet<>() {{  // [ab.zyx1, ab.zyx2]
            add("ab.zyx1");
            add("ab.zyx2");
        }};

        im.fromDir(testDir);
        sm.fromDir(testDir);

        Set<String> sounds = sm.getSounds().keySet();
        Set<String> images = im.getImages().keySet();

        assertEquals(images, expectedImages);
        assertEquals(sounds, expectedSounds);
    }
}