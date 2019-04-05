package io.github.jgame.util;

import io.github.jgame.image.ImageManager;
import io.github.jgame.mixer.SoundManager;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static io.github.jgame.util.FileUtils.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Also includes tests for {@link FileUtils}
 */
public class ResourceManagerTest {
    private File testDir = new File("testFromDir");

    @Test
    public void testTouchFileAndFolder() {
        assertTrue(touchFile("testFromDir/ab/zyx1.png"));
        assertTrue(touchFile("testFromDir/ab/zyx2.png"));

        assertTrue(touchFile("testFromDir/ab/ac/gh1.wav"));
        assertTrue(touchFile("testFromDir/ab/ac/gh2.wav"));

        assertTrue(touchFolder("testFromDir/notAFileEither.wav"));
        assertTrue(touchFolder("testFromDir/imNotAFile.png"));
    }

    @Test(dependsOnMethods = "testFromDir")
    public void testDeleteDir() throws IOException {
        assertTrue(deleteDir(testDir));
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