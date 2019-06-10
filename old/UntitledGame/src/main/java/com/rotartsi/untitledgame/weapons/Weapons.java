package com.rotartsi.untitledgame.weapons;

import com.rotartsi.jgame.gameLogic.Weapon;
import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.mechanics.Meters;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.untitledgame.bullets.Bullets;
import com.rotartsi.untitledgame.states.TestingState;

import java.awt.*;

import static com.rotartsi.jgame.util.StringManager.fmt;

public class Weapons {
    public static class TestWeapon extends Weapon {
        private TestingState game;
        private String cooldownTxt = "UNINIT";

        public TestWeapon(PlatformerPlayer own, TestingState game, ScreenBounds bounds) {
            super(100, 5, new Bullets.TestBulletFactory(game, bounds), own, 30, new Meters.AmmoMeter(30), 3000);
            this.game = game;
        }

        @Override
        public void onAmmoUpdate() {
            if (game.player.currentWeapon == this) {
                game.ammo.updateImage(ImageManager.fromText(game.player.currentWeapon.ammo + "/" + game.player.currentWeapon.meter.reserve,
                        game.font, Color.BLACK));
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (reloading) {
                String txt = fmt("%.1f", manager.currentAction.getActionLeft() / 1000d);
                if (!txt.equals(cooldownTxt)) {
                    game.cooldown.updateImage(ImageManager.fromText(txt, game.font, Color.BLACK));
                    cooldownTxt = txt;
                }
            }
        }
    }
}
