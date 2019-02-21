package io.github.jgame.gui;

import io.github.jgame.sprite.Group;
import io.github.jgame.sprite.Sprite;

import java.awt.*;
import java.util.LinkedList;

public class ButtonManager {
    private static Group buttons = new Group();

    public static void register(Sprite bt) {
        buttons.add(bt);
    }

    public static void blit(Graphics2D screen) {
        for (Sprite bt : buttons.sprites) {
            if (bt.visible) {
                bt.blit(screen);
            }
        }
    }

    public static void update(Sprite mouseSprite) {
        LinkedList<Sprite> hovering = mouseSprite.collidesWith(buttons);
        for (Sprite bt : buttons.sprites) {
            if (bt.active) {
                bt.updateHover(hovering.contains(bt), mouseSprite.pos);
            }
        }
    }

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
