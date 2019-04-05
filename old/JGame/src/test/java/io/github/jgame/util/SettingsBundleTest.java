package io.github.jgame.util;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static io.github.jgame.Constants.JGameStr;
import static io.github.jgame.Constants.settings;
import static org.testng.Assert.assertEquals;

public class SettingsBundleTest {
    @Test
    public void testAll() throws IOException, URISyntaxException {
        File file = File.createTempFile("settingsTest", ".properties");
        SettingsBundle bundle = new SettingsBundle(file);

        bundle.set("testVal", "Lorem Ipsum dolor sit amet");
        bundle.save();

        bundle = new SettingsBundle(file);
        assertEquals(bundle.get("testVal"), "Lorem Ipsum dolor sit amet");
        file.delete();
    }

    @Test
    public void testJGameSettings() {
        assertEquals(settings.get("testing.verifyStr"), "Hello World!");
        assertEquals(JGameStr.getString("testing.verifyStr"), "Hello World!");
    }
}