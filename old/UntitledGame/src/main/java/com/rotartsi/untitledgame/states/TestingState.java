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
import com.rotartsi.untitledgame.bullets.CustomBulletClass;
import com.rotartsi.untitledgame.characters.BasicCharacter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class TestingState extends State {
    public BasicCharacter player;
    public PlatformerObstacle dummyObstacle = new PlatformerObstacle();
    public SurfaceMap map;
    public Sprite cooldown;
    public Group obstacles = new Group();
    public Font font = new Font("Times New Roman", Font.PLAIN, 25);
    private SurfaceMap gui;
    public Sprite ammo;
    private double[] zoom = new double[]{2, 1};
    public Group bullets = new Group();
    private Vector2 guiCenter;
    private double[] guiZoom = new double[]{1, 1};

    public TestingState(Game parent) {
        // Put code here that you only want to build once. (like the obstacles)
        super(parent);
        PlatformerObstacle testObstacle = new PlatformerObstacle();
        testObstacle.updateImage(new BufferedImage(100, 750, TYPE_INT_ARGB));
        testObstacle.pos = new Vector2(700, 500);
        testObstacle.updateRect();

        obstacles.add(testObstacle);
    }

    @Override
    public void enter(String old_state) {
        // Put code here that you want to build every time the state reloads. (like the player)
        map = new SurfaceMap(2000, 1000, TYPE_INT_ARGB);
        gui = new SurfaceMap(800, 600, TYPE_INT_ARGB);

        guiCenter = new Vector2(gui.img.getWidth() / 2, gui.img.getHeight() / 2);

        ScreenBounds bounds = new ScreenBounds(new Rectangle2D.Double(0, 0, map.img.getWidth(), map.img.getHeight() - 50),
                dummyObstacle, dummyObstacle, dummyObstacle, dummyObstacle);

        player = new BasicCharacter(new BufferedImage(50, 50, TYPE_INT_ARGB),
                bounds, this);
        try {
            player.keybinds = new SettingsBundle("settings.properties");
        } catch (IOException e) {
            player.keybinds = new SettingsBundle();
        }
        player.collidables = obstacles;
        ammo = new Sprite(ImageManager.fromText(player.currentWeapon.ammo + "/" + player.currentWeapon.meter.reserve,
                font, Color.BLACK));
        ammo.pos = new Vector2(100, 50);
        ammo.updateRect();

        cooldown = new Sprite(new BufferedImage(1, 1, TYPE_INT_ARGB));
        cooldown.pos = new Vector2(700, 50);
        cooldown.updateRect();
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
        for (int i = 0; i < bullets.sprites.size(); i++) {
            bullets.sprites.get(i).update();
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
        gui.clear();

        player.blitTo(map.g2d);

        map.g2d.setColor(Color.GREEN);
        map.g2d.fillRect((int) player.rect.x, (int) player.rect.y, (int) player.rect.width, (int) player.rect.height);

        for (int i = 0; i < obstacles.sprites.size(); i++) {
            PlatformerObstacle obstacle = (PlatformerObstacle) obstacles.sprites.get(i);
            obstacle.blitTo(map.g2d);

            map.g2d.setColor(Color.BLACK);
            map.g2d.fillRect((int) obstacle.rect.x, (int) obstacle.rect.y, (int) obstacle.rect.width, (int) obstacle.rect.height);
        }

        for (int i = 0; i < bullets.sprites.size(); i++) {
            CustomBulletClass bullet = (CustomBulletClass) bullets.sprites.get(i);
            bullet.blitRect(map.g2d);

            map.g2d.setColor(Color.YELLOW);
            map.g2d.fillRect((int) bullet.rect.x, (int) bullet.rect.y, (int) bullet.rect.width, (int) bullet.rect.height);
        }

        ammo.blitTo(gui.g2d);
        if (player.currentWeapon.reloading) {
            cooldown.blitTo(gui.g2d);
        }

        Dimension screen = this.game.runner.getSize();
        Vector2 center = new Vector2(screen.width / 2d, screen.height / 2d);
        map.getScroll(player.pos, screen, center, true, false);
        map.getResized(screen, zoom); // this should have the same aspect ratio as the map
        map.blitTo(g2d);

        gui.getScroll(guiCenter, screen, center, true, true);
        gui.getResized(screen, guiZoom);
        gui.blitTo(g2d);

        g2d.dispose();
    }

    private class TestingMouseHandler extends MouseHandler {

    }
}
