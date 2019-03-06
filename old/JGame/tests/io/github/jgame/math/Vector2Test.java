package io.github.jgame.math;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;
import static org.testng.Assert.assertEquals;

public class Vector2Test {

    private Vector2 origin;
    private Vector2 hundred;

    @BeforeSuite
    public void setUp() {
        origin = new Vector2(0, 0);
        hundred = new Vector2(100, -100);
    }

    @Test
    public void testAdd() {
        assertEquals(origin.add(hundred).toInt(), new int[]{100, -100});
        assertEquals(hundred.add(origin).toInt(), new int[]{100, -100});
    }

    @Test
    public void testSubtract() {
        assertEquals(origin.subtract(hundred).toInt(), new int[]{-100, 100});
        assertEquals(hundred.subtract(origin).toInt(), new int[]{100, -100});
    }

    @Test
    public void testToInt() {
        assertEquals(origin.toInt(), new int[]{0, 0});
        assertEquals(hundred.toInt(), new int[]{100, -100});
    }

    @Test
    public void testAngleTo() {
        assertEquals(origin.angleTo(hundred), 45);
        assertEquals(hundred.angleTo(origin), 225);
    }

    @Test
    public void testVelocityTo() {
        assertEquals(origin.velocityTo(hundred, 1).toString(),
                fmt(JGameStr.getString("math.Vector2.toStringFormat"), 0.7071067811865475d, -0.7071067811865475d));
        assertEquals(hundred.velocityTo(origin, 1).toString(),
                fmt(JGameStr.getString("math.Vector2.toStringFormat"), -0.7071067811865475d, 0.7071067811865475d));
    }

    @Test
    public void testDistanceTo() {
        assertEquals(origin.distanceTo(hundred), 141.4213562373095);
        assertEquals(hundred.distanceTo(origin), 141.4213562373095);
    }

    @Test
    public void testToString() {
        assertEquals(hundred.toString(), fmt(JGameStr.getString("math.Vector2.toStringFormat"), 100d, -100d));
        assertEquals(origin.toString(), fmt(JGameStr.getString("math.Vector2.toStringFormat"), 0d, 0d));
    }
}