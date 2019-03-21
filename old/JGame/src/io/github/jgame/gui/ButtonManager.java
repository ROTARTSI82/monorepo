package io.github.jgame.gui;

import io.github.jgame.sprite.Group;
import io.github.jgame.sprite.Sprite;

import java.awt.*;
import java.util.LinkedList;

/**
 * Handles hovering over buttons and clicking them.
 * <p>
 * Also see {@link Group}
 */
public class ButtonManager {

    /**
     * The group of all registered buttons to check for clicks and hovers.
     */
    private static Group buttons = new Group();

    /**
     * Add the sprite to the clickable sprites group.
     * <p>
     * This sprite's {@code onClick} and {@code updateHover} would be called accordingly by the {@code ButtonManager}
     *
     * @param bt Sprite to add
     */
    public static void register(Sprite bt) {
        buttons.add(bt);
    }

    /**
     * Blit all sprites labled as "visible" in the group to the screen.
     *
     * @param screen Graphics to blit to.
     */
    public static void blit(Graphics2D screen) {
        for (Sprite bt : buttons.sprites) {
            if (bt.visible) {
                bt.blit(screen);
            }
        }
    }

    /**
     * Call each sprite's {@code updateHover} function accordingly.
     * This should be called once per frame.
     *
     * @param mouseSprite Sprite to use for the mouse to check if the user is hovering over a specific sprite.
     */
    public static void update(Sprite mouseSprite) {
        LinkedList<Sprite> hovering = mouseSprite.collidesWith(buttons);
        for (Sprite bt : buttons.sprites) {
            if (bt.active) {
                bt.updateHover(hovering.contains(bt), mouseSprite.pos);
            }
        }
    }

    /**
     * Call each sprite's {@code onClick} function accordingly.
     * This should be called when the user clicks the mouse.
     *
     * @param mouseSprite Sprite used to track mouse. This determines which sprites are being clicked.
     */
    public static void mouseClicked(Sprite mouseSprite) {
        LinkedList<Sprite> hovering = mouseSprite.collidesWith(buttons);
        for (Sprite bt : buttons.sprites) {
            if (bt.active && hovering.contains(bt)) {
                // TODO: Should we call updateHover when onClick is generated?
                //bt.updateHover(true, mouseSprite.pos);
                bt.onClick(mouseSprite.pos);
            }
        }
    }
}
