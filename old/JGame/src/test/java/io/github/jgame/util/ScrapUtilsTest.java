package io.github.jgame.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ScrapUtilsTest {

    @Test
    public void testClipboard() {
        assertNotNull(ScrapUtils.getContents());

        ScrapUtils.setClipboard("Hello World!");
        assertEquals(ScrapUtils.getContents(), "Hello World!");
        assertEquals(ScrapUtils.getContents(), "Hello World!");
    }
}