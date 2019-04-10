package com.rotartsi.jgame.image;

import com.rotartsi.jgame.math.Vector2;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.testng.Assert.assertEquals;

public class SurfaceMapTest {
    private SurfaceMap testMap;
    private Dimension screen;

    @BeforeSuite
    public void setUp() {
        testMap = new SurfaceMap(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        screen = new Dimension(100, 100);
    }

    @Test(dependsOnMethods = {"testGetScroll"})
    public void testGetPos() {
        Vector2 onSurface = testMap.getPos(new Vector2(50, 50));
        assertEquals(onSurface.toInt(), new int[]{500, 500});
    }

    @Test(dependsOnMethods = {"testGetScroll"})
    public void testFromPos() {
        Vector2 onScreen = testMap.fromPos(new Vector2(500, 500));
        assertEquals(onScreen.toInt(), new int[]{50, 50});
    }

    @Test
    public void testGetResized() {
        BufferedImage resized = testMap.getResized(screen, new double[]{2, 2});

        assertEquals(resized.getHeight(), 200);
        assertEquals(resized.getWidth(), 200);
    }

    @Test(dependsOnMethods = {"testGetResized"})
    public void testGetScroll() {
        Vector2 scroll = testMap.getScroll(new Vector2(500, 500), screen,
                new Vector2(50, 50), true, true);
        assertEquals(scroll.toInt(), new int[]{-50, -50});
    }
}