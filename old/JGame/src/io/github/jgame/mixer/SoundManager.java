package io.github.jgame.mixer;

import java.util.HashMap;
import java.util.logging.Logger;

public class SoundManager {
    private Logger logger;
    private HashMap<String, Sound> sounds = new HashMap<>();

    public SoundManager() {
    }

    public void fromFile(String file, String id) {
        sounds.put(id, new Sound(file, Sound.LoadModes.FROM_FILE));
    }

    public void fromURL(String url, String id) {
        sounds.put(id, new Sound(url, Sound.LoadModes.FROM_URL));
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
