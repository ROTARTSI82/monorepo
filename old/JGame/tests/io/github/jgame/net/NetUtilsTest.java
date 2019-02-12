package io.github.jgame.net;

import org.testng.annotations.Test;

import java.net.DatagramPacket;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class NetUtilsTest {

    @Test
    public void testExtractString() {
        assertEquals(NetUtils.extractString(
                new DatagramPacket(new byte[]{76, 111, 114, 101, 109, 32, 105, 112, 115, 117, 109}, 11)),
                "Lorem ipsum");
    }

    @Test
    public void testSerialize() {
        HashMap<String, Object> dat = new HashMap<>();
        dat.put("int", 153);
        dat.put("double", 3.1415d);
        dat.put("arr", new byte[]{0, 2, 3});

        byte[] serial = NetUtils.serialize(dat);
        HashMap<String, Object> out = NetUtils.deserialize(serial);

        assertNotNull(out);
        assertEquals(out.get("int"), 153);
        assertEquals(out.get("double"), 3.1415d);
        assertEquals(out.get("arr"), new byte[]{0, 2, 3});

    }
}