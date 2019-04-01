package io.github.jgame.sprite;

import java.util.LinkedList;

/**
 * A list of sprites that can be used for collision detection
 */
public class Group {

    /**
     * The internal list of sprites.
     */
    public LinkedList<Sprite> sprites = new LinkedList<>();

    /**
     * Add a sprite to our list, and add us to the sprite's list of groups it's in.
     *
     * @param sprite Sprite to add
     */
    public void add(Sprite sprite) {
        sprites.add(sprite);
        sprite.groups.add(this);
    }

    /**
     * Remove the sprite from our list and remove ourselves from the sprite's list of groups it's in.
     *
     * @param sprite Sprite to remove.
     */
    public void remove(Sprite sprite) {
        sprites.remove(sprite);
        sprite.groups.remove(this);
    }
}
