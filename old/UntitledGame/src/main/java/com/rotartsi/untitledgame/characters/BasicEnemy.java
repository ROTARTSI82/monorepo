package com.rotartsi.untitledgame.characters;

import com.rotartsi.jgame.gameLogic.Weapon;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.untitledgame.states.TestingState;
import com.rotartsi.untitledgame.weapons.Weapons;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class BasicEnemy extends PlatformerPlayer {
    public Weapon currentWeapon;
    private LinkedList<Weapon> weapons = new LinkedList<>();
    private TestingState state;

    public BasicEnemy(BufferedImage img, ScreenBounds bounds, TestingState game) {
        super(img, bounds);
        this.health = 200;
        setBounds(bounds);
        weapons.add(new Weapons.TestWeapon(this, game, bounds));
        currentWeapon = weapons.get(0);
        this.state = game;
    }

    @Override
    public void update() {
        super.update();
        currentWeapon.tick();
        //System.out.println("FIRE");

        // We need to do this so that dragging works.
        currentWeapon.tryFire(state.player.pos, false);

        internalState.put("jump", true);

        if (state.player.pos.x > this.pos.x) {
            internalState.put("forward", true);
            internalState.put("backward", false);
        } else {
            internalState.put("forward", false);
            internalState.put("backward", true);
        }
    }

    @Override
    public void damage(double amount) {
        System.out.println("ENEMY WAS HIT FOR");
        this.health -= amount;
        if (health <= 0) {
            kill();
        }
    }
}
