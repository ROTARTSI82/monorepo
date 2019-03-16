package io.github.jgame.mixer;

import io.github.jgame.Constants;
import io.github.jgame.util.ResourceManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: add javadoc
 */
public class SoundManager extends ResourceManager {

    private Logger logger;
    private HashMap<String, LinkedList<Sound>> sounds = new HashMap<>();

    public SoundManager() {
        super();
    }

    public void addSound(String id, Sound snd) {
        if (sounds.containsKey(id)) {
            sounds.get(id).add(snd);
        } else {
            sounds.put(id, new LinkedList<>() {{
                add(snd);
            }});
        }
    }

    /*
    public static void main(String[] args) {
        SoundManager x = new SoundManager();
        ImageManager y = new ImageManager();
        x.fromDir(new File("/Users/Rotartsi/Documents/tst"));
        y.fromDir(new File("/Users/Rotartsi/Documents/tst"));
    }
     */

    @Override
    public void addFile(File file, int baseLen) {
        String id = getId(file, baseLen);
        try {
            addSound(id, new Sound(file.toURI().toURL()));
        } catch (MalformedURLException e) {
            logger.log(Level.WARNING, "Failed to load file: ", e);
        }
    }

    @Override
    public LinkedList<String> getExtensions() {
        return new LinkedList<>() {{
            add(".wav");
            add(".mp3");
        }};
    }

    public void fromFile(String file, String id) {
        addSound(id, new Sound(file));
    }

    public void fromURL(URL url, String id) {
        addSound(id, new Sound(url));
    }

    public Sound get(String id) {
        System.out.println(sounds);
        LinkedList<Sound> arr = sounds.get(id);
        return arr.get(Constants.rand.nextInt(arr.size()));
    }

    public void play(String id, boolean reset, float volume) {
        Sound playSound = get(id);
        playSound.setVolume(volume);
        playSound.play(reset);
    }
}
