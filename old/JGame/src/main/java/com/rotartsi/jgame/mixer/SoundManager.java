package com.rotartsi.jgame.mixer;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.util.ResourceManager;

import javax.sound.sampled.FloatControl;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.rotartsi.jgame.Constants.JGameStr;

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
        System.out.println(x.getSounds());
        System.out.println(y.getImages());
    }*/

    /**
     * Get the internal HashMap of sounds. See {@link #sounds}
     *
     * @return HashMap of all sounds
     */
    public HashMap<String, LinkedList<Sound>> getSounds() {
        return sounds;
    }

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
        String id = getId(file, baseLen, true);
        // System.out.println("Adding " + file + " as id "+ id);
        try {
            addSound(id, new Sound(file.toURI().toURL()));
        } catch (MalformedURLException e) {
            logger.log(Level.WARNING, JGameStr.getString("loadFail"), e);
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
            add("wav");
            add("mp3");
            add("ogg");  // Maybe?
        }};
    }

    /**
     * Get a random sound assigned to the identifier.
     *
     * @param id id
     * @return Sound
     */
    public Sound get(String id) {
        if (sounds.containsKey(id)) {
            LinkedList<Sound> arr = sounds.get(id);
            return arr.get(Constants.rand.nextInt(arr.size()));
        } else {
            return null;
        }
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
        if (playSound != null) {
            playSound.setVal(volume, FloatControl.Type.MASTER_GAIN);
            playSound.play(reset);
        }
    }
}
