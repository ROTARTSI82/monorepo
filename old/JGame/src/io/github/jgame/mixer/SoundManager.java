package io.github.jgame.mixer;

import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * TODO: add javadoc
 */
public class SoundManager {
    private Logger logger;
    private HashMap<String, Sound> sounds = new HashMap<>();

    public SoundManager() {
    }

    public void fromFile(String file, String id) {
        sounds.put(id, new Sound(file));
    }

    public void fromURL(URL url, String id) {
        sounds.put(id, new Sound(url));
    }

    public Sound get(String id) {
        return sounds.get(id);
    }

    public void play(String id, boolean reset, float volume) {
        Sound playSound = get(id);
        playSound.setVolume(volume);
        playSound.play(reset);
    }
}
