package com.rotartsi.untitledgame.weapons;

import com.rotartsi.jgame.gameLogic.Weapon;
import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.mechanics.Meters;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;
import com.rotartsi.untitledgame.bullets.Bullets;
import com.rotartsi.untitledgame.states.TestingState;

import java.awt.*;

public class Weapons {
    public static class TestWeapon extends Weapon {
        private TestingState game;

        public TestWeapon(PlatformerPlayer own, TestingState game) {
            super(100, 5, new Bullets.TestBulletFactory(game), own, 30, new Meters.AmmoMeter(30), 3000);
            this.game = game;
        }

        @Override
        public void onAmmoUpdate() {
            if (game.player.currentWeapon == this) {
                game.ammo.updateImage(ImageManager.fromText(game.player.currentWeapon.ammo + "/" + game.player.currentWeapon.meter.reserve,
                        new Font("Times New Roman", Font.PLAIN, 25), Color.BLACK));
            }
        }
    }
}
