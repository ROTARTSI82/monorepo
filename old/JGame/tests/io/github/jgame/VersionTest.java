package io.github.jgame;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class VersionTest {

    @Test
    public void testFromAbsolute() {
        Version fromAbs = Version.fromAbsolute(168364039);
        assertEquals(fromAbs.modeValue, 10);
        assertEquals(fromAbs.major, 9);
        assertEquals(fromAbs.minor, 8);
        assertEquals(fromAbs.micro, 7);

        assertEquals(fromAbs.getValue(), 168364039);
    }

    @Test
    public void testToString() {
        Version str = new Version("xxx%syyy", 127, 255, 64, 32);
        assertEquals(str.toString(), "xxx255.64.32yyy");
    }

    @Test
    public void testGetValue() {
        Version val = new Version("%s", 0, 128, 64, 255);
        assertEquals(val.getValue(), 8405247);

        assertEquals(Version.fromAbsolute(8405247).toString(), val.toString());
    }
}