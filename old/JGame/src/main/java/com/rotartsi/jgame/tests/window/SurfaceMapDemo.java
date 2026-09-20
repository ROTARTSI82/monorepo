package com.rotartsi.jgame.tests.window;

import com.rotartsi.jgame.game.Game;
import com.rotartsi.jgame.game.GameRunner;
import com.rotartsi.jgame.game.State;
import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.image.SurfaceMap;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.sprite.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static com.rotartsi.jgame.Constants.rand;

/*
THIS CODE SHOULD NOT BE IN PRODUCTION. IF IT IS, SOMEBODY SCREWED UP!
 */

/**
 * @deprecated this shouldn't be here!
 */
@Deprecated
public class SurfaceMapDemo extends GameRunner {

    public SurfaceMapDemo(Game gameInst) {
        super(gameInst);
    }

    public static void main(String[] args) {
        run(new DemoGame());
    }

    static class DemoGame extends Game {
        public DemoGame() {
            states.put("main", new DemoState(this));
        }

        @Override
        public Timer[] getTimers() {
            return new Timer[]{
                    new Timer(0, e -> {
                        synchronized (this) {
                            updateLogic();
                        }
                    }),
                    new Timer(1000 / 15, e -> {
                        synchronized (this) {
                            handleRepaint();
                        }
                    })};
        }
    }

    static class DemoState extends State {
        Font font = new Font("Times New Roman", Font.PLAIN, 12);
        SurfaceMap map = new SurfaceMap(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        Sprite player = new Sprite(ImageManager.fromText("PLAYER", font, Color.BLACK));
        Group background = new Group();

        public DemoState(Game parent) {
            super(parent);
            BufferedImage backgroundIMG = ImageManager.fromText("OBSTACLE", font, Color.BLACK);
            for (int i = 0; i < 100; i++) {
                Sprite nsprite = new Sprite(backgroundIMG);
                nsprite.pos = new Vector2(rand.nextInt(1000), rand.nextInt(1000));
                background.add(nsprite);
            }
        }

        @Override
        public void updateGraphics(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            map.fill(Color.WHITE);
            //map.clear();
            player.blitTo(map.g2d);
            for (Sprite obstacle : background.sprites) {
                obstacle.blitTo(map.g2d);
            }

            Dimension screen = this.game.runner.getSize();
            map.getScroll(player.pos, screen, new Vector2(screen.width / 2d, screen.height / 2d), true, true);
            map.getResized(screen, new double[]{3, 3});
            map.blitTo(g2d);
            g2d.dispose();
        }

        @Override
        public void updateLogic() {
            player.pos = player.pos.add(player.vel);
            player.updateRect();
        }

        @Override
        public KeyHandler[] getKeyHandlers() {
            return new KeyHandler[]{new DemoKeyHandle()};
        }

        class DemoKeyHandle extends KeyHandler {
            int speed = 5;

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A: {
                        player.vel.x = -speed;
                        break;
                    }
                    case KeyEvent.VK_D: {
                        player.vel.x = speed;
                        break;
                    }
                    case KeyEvent.VK_W: {
                        player.vel.y = -speed;
                        break;
                    }
                    case KeyEvent.VK_S: {
                        player.vel.y = speed;
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_D: {
                        player.vel.x = 0;
                        break;
                    }
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_S: {
                        player.vel.y = 0;
                        break;
                    }
                }
            }
        }
    }
}
