package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.event.AnyEvent;
import com.rotartsi.jgame.game.Game;
import com.rotartsi.jgame.game.GameRunner;
import com.rotartsi.jgame.game.State;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.sprite.Sprite;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.jgame.util.SettingsBundle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;


/*
THIS CODE SHOULD NOT BE IN PRODUCTION. IF IT IS, SOMEBODY SCREWED UP!
 */
public class PlayerTest extends GameRunner {
    public PlayerTest(Game g) {
        super(g);
    }

    public static void main(String[] args) {
        run(new PlayerTestGame());
    }

    private static class PlayerTestGame extends Game {
        PlayerTestGame() {
            super();
            states.put("main", new PlayerTestState(this));
        }
    }

    private static class PlayerTestState extends State {
        PlatformerEntity player;

        PlayerTestState(Game p) {
            super(p);
        }

        @Override
        public void enter(String old_state) {
            Dimension size = getScreenSize();
            PlatformerObstacle ob = new PlatformerObstacle();
            ScreenBounds bounds = new ScreenBounds(new Rectangle2D.Double(0, 0, size.width, size.height),
                    ob, ob, ob, ob);
            player = new PlatformerEntity(Sprite.defaultImage, bounds);
            player.collidables = new Group();
            ob.pos = new Vector2(700, 850);
            ob.updateRect();
            player.collidables.add(ob);
            player.keybinds = new SettingsBundle();
        }

        private class PlayerKeyHandler extends KeyHandler {
            @Override
            public void keyPressed(KeyEvent e) {
                player.updateEvent(new AnyEvent(e));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.updateEvent(new AnyEvent(e));
            }
        }

        @Override
        public KeyHandler[] getKeyHandlers() {
            return new KeyHandler[]{new PlayerKeyHandler()};
        }

        @Override
        public void updateGraphics(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            player.blitTo(g2d);
            player.blitRect(g2d);
            for (Sprite sp : player.collidables.sprites) {
                sp.blitTo(g2d);
                sp.blitRect(g2d);
            }
        }

        @Override
        public void updateLogic() {
            Dimension size = getScreenSize();
            PlatformerObstacle ob = new PlatformerObstacle();
            ScreenBounds bounds = new ScreenBounds(new Rectangle2D.Double(0, 0, size.width, size.height),
                    ob, ob, ob, ob);
            player.setBounds(bounds);
            player.update();
        }
    }
}
