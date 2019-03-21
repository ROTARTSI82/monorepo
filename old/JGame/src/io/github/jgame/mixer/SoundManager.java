package io.github.jgame.mixer;

import io.github.jgame.Constants;
import io.github.jgame.util.ResourceManager;

import javax.sound.sampled.FloatControl;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manage and play sounds
 */
public class SoundManager extends ResourceManager {

    /**
     * Internal logger object used to log events.
     */
    private Logger logger;

    /**
     * The internal list of sounds that are loaded.
     */
    private HashMap<String, LinkedList<Sound>> sounds = new HashMap<>();

    /**
     * Create new sound manager.
     */
    public SoundManager() {
        super();
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Add a sound to the id (multiple sounds can be applied to an id, and a random one would be played upon
     * a call to {@link #get} or {@link #play}
     *
     * @param id  Id
     * @param snd sound
     */
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

    /**
     * Adding a file from the {@link #fromDir(File)}. The identifier is derived from removing {@code baseLen}
     * characters from the front of the string.
     *
     * <b>THE USER SHOULD NEVER CALL THIS FUNCTION!</b>
     *
     * @param file    File
     * @param baseLen length of base path (to be removed to get the identifier.)
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

    /**
     * Accepted extensions of files to add when {@link #fromDir(File)} is called.
     *
     * @return list of extensions
     */
    @Override
    public LinkedList<String> getExtensions() {
        return new LinkedList<>() {{
            add(".wav");
            add(".mp3");
        }};
    }

    /**
     * Get a random sound assigned to the identifier.
     *
     * @param id id
     * @return Sound
     */
    public Sound get(String id) {
        System.out.println(sounds);
        LinkedList<Sound> arr = sounds.get(id);
        return arr.get(Constants.rand.nextInt(arr.size()));
    }

    /**
     * Play a random sound assigned to the identifier at this volume.
     *
     * @param id id
     * @param reset {@link Sound}.play()'s reset param.
     * @param volume volume.
     */
    public void play(String id, boolean reset, float volume) {
        Sound playSound = get(id);
        playSound.setVal(volume, FloatControl.Type.MASTER_GAIN);
        playSound.play(reset);
    }
}
