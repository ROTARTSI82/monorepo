package com.rotartsi.jgame.tests.window;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.event.EventRobot;
import com.rotartsi.jgame.game.Game;
import com.rotartsi.jgame.game.State;
import com.rotartsi.jgame.image.SurfaceMap;
import com.rotartsi.jgame.math.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @deprecated Use SurfaceMapTest. Only useful for testing blitting.
 */
@Deprecated
public class WindowCaptureState extends State {
    // Make these volatile?
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
    public void updateLogic() {
        BufferedImage sprite = robot.createScreenCapture(area);
        map.getGraphics().drawImage(sprite, 0, 0, null);
        Dimension d = game.runner.getSize();
        map.getScroll(new Vector2(0, 0), d, new Vector2(0, 0), true, true);
        map.getResized(d, new double[]{1, 1});
    }

    @Override
    public void updateGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        map.blitTo(g2d);
        g2d.dispose();
    }
}
