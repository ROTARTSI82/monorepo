package io.github.jgame.math;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class Vector2Test {

    private Vector2 origin;
    private Vector2 hundred;

    @BeforeMethod
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
        assertEquals(origin.angleTo(hundred), -135);
        assertEquals(hundred.angleTo(origin), 45);
    }

    @Test
    public void testVelocityTo() {
        assertEquals(origin.velocityTo(hundred, 1).toString(),
                "Vector2[0.7071067811865475, -0.7071067811865475]");
        assertEquals(hundred.velocityTo(origin, 1).toString(),
                "Vector2[-0.7071067811865475, 0.7071067811865475]");
    }

    @Test
    public void testDistanceTo() {
        assertEquals(origin.distanceTo(hundred), 141.4213562373095);
        assertEquals(hundred.distanceTo(origin), 141.4213562373095);
    }

    @Test
    public void testToString() {
        assertEquals(hundred.toString(), "Vector2[100.0, -100.0]");
        assertEquals(origin.toString(), "Vector2[0.0, 0.0]");
    }
}