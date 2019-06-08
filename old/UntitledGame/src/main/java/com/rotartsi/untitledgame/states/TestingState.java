package com.rotartsi.untitledgame.states;

import com.rotartsi.jgame.event.AnyEvent;
import com.rotartsi.jgame.game.Game;
import com.rotartsi.jgame.game.State;
import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.image.SurfaceMap;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mechanics.PlatformerObstacle;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.sprite.Sprite;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.jgame.util.SettingsBundle;
import com.rotartsi.untitledgame.characters.BasicCharacter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TestingState extends State {
    public BasicCharacter player;
    public PlatformerObstacle dummyObstacle = new PlatformerObstacle();
    public SurfaceMap map;
    public Sprite ammo;
    public Group bullets = new Group();
    private Group obstacles = new Group();

    public TestingState(Game parent) {
        // Put code here that you only want to build once. (like the obstacles)
        super(parent);
        PlatformerObstacle testObstacle = new PlatformerObstacle();
        testObstacle.updateImage(new BufferedImage(100, 750, BufferedImage.TYPE_INT_ARGB));
        testObstacle.pos = new Vector2(700, 500);
        testObstacle.updateRect();

        obstacles.add(testObstacle);
    }

    @Override
    public void enter(String old_state) {
        // Put code here that you want to build every time the state reloads. (like the player)
        map = new SurfaceMap(2000, 1000, BufferedImage.TYPE_INT_ARGB);

        ScreenBounds bounds = new ScreenBounds(new Rectangle2D.Double(0, 0, map.img.getWidth(), map.img.getHeight() - 50),
                dummyObstacle, dummyObstacle, dummyObstacle, dummyObstacle);

        player = new BasicCharacter(new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB),
                bounds, this);
        try {
            player.keybinds = new SettingsBundle("settings.properties");
        } catch (IOException e) {
            player.keybinds = new SettingsBundle();
        }
        player.collidables = obstacles;
        ammo = new Sprite(ImageManager.fromText(player.currentWeapon.ammo + "/" + player.currentWeapon.meter.reserve,
                new Font("Times New Roman", Font.PLAIN, 25), Color.BLACK));
        ammo.pos = new Vector2(200, 50);
        ammo.updateRect();
    }

    @Override
    public void onEvent(AnyEvent event) {
        player.updateEvent(event);
    }

    @Override
    public void updateLogic() {
        //updateBounds();
        player.update();
//        System.out.println("bullets.sprites.size() = " + bullets.sprites.size());
        for (Sprite bullet : bullets.sprites) {
            bullet.update();
        }
    }

    @Override
    public MouseHandler[] getMouseHandlers() {
        return new MouseHandler[]{new TestingMouseHandler()};
    }

    @Override
    public void updateGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        map.fill(Color.WHITE);

        player.blitTo(map.g2d);

        map.g2d.setColor(Color.GREEN);
        map.g2d.fillRect((int) player.rect.x, (int) player.rect.y, (int) player.rect.width, (int) player.rect.height);

        for (Sprite obstacle : obstacles.sprites) {
            obstacle.blitTo(map.g2d);

            map.g2d.setColor(Color.BLACK);
            map.g2d.fillRect((int) obstacle.rect.x, (int) obstacle.rect.y, (int) obstacle.rect.width, (int) obstacle.rect.height);
        }

        for (Sprite bullet : bullets.sprites) {
            bullet.blitRect(map.g2d);

            map.g2d.setColor(Color.YELLOW);
            map.g2d.fillRect((int) bullet.rect.x, (int) bullet.rect.y, (int) bullet.rect.width, (int) bullet.rect.height);
        }

        Dimension screen = this.game.runner.getSize();
        map.getScroll(player.pos, screen, new Vector2(screen.width / 2d, screen.height / 2d), true, false);
        map.getResized(screen, new double[]{2, 1}); // this should have the same aspect ratio as the map
        map.blitTo(g2d);
        ammo.blitTo(g2d);
        g2d.dispose();
    }

    private class TestingMouseHandler extends MouseHandler {

    }
}
