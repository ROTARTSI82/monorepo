package com.rotartsi.untitledgame.characters;

import com.rotartsi.jgame.gameLogic.Weapon;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.untitledgame.states.TestingState;
import com.rotartsi.untitledgame.weapons.Weapons;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class BasicCharacter extends PlatformerPlayer {
    public Weapon currentWeapon;
    private LinkedList<Weapon> weapons = new LinkedList<>();
    private TestingState state;

    public BasicCharacter(BufferedImage img, ScreenBounds bounds, TestingState game) {
        super(img, bounds);
        setBounds(bounds);
        weapons.add(new Weapons.TestWeapon(this, game, bounds));
        currentWeapon = weapons.get(0);
        this.state = game;
    }

    @Override
    public void update() {
        super.update();
        currentWeapon.tick();
        if (internalState.get("firing")) {
            //System.out.println("FIRE");

            // We need to do this so that dragging works.
            Point ml = MouseInfo.getPointerInfo().getLocation();
            Point screenLoc = state.game.getLocationOnScreen();
            currentWeapon.tryFire(new Vector2(ml.x - screenLoc.x, ml.y - screenLoc.y), false);
        }
    }
}
