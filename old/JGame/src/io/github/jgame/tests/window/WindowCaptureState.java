package io.github.jgame.tests.window;

import io.github.jgame.Constants;
import io.github.jgame.event.EventRobot;
import io.github.jgame.game.Game;
import io.github.jgame.game.State;
import io.github.jgame.image.SurfaceMap;
import io.github.jgame.math.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WindowCaptureState extends State {
    private EventRobot robot;
    private Rectangle area;
    private SurfaceMap map;

    public WindowCaptureState(Game game) {
        super(game);
        area = new Rectangle(0, 0, 1440, 900);
        try {
            robot = new EventRobot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        map = new SurfaceMap(1440, 900, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void updateGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        BufferedImage sprite = robot.createScreenCapture(area);
        map.getGraphics().drawImage(sprite, 0, 0, null);
        Dimension d = game.runner.getSize();
        map.getScroll(new Vector2(0, 0), d, new Vector2(0, 0), true, true);
        map.getResized(d, new double[]{1, 1});
        map.blitTo(g2d);
        g2d.dispose();
    }
}
