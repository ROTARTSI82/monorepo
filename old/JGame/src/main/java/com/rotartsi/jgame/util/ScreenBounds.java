package com.rotartsi.jgame.util;

import com.rotartsi.jgame.mechanics.PlatformerObstacle;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;

import java.awt.geom.Rectangle2D;

import static com.rotartsi.jgame.util.StringManager.fmt;

public class ScreenBounds {
    public double[] maxCoords;
    public double[] minCoords;

    public PlatformerObstacle left;
    public PlatformerObstacle right;
    private PlatformerObstacle top;
    public PlatformerObstacle bottom;

    public ScreenBounds(Rectangle2D.Double area, PlatformerObstacle top, PlatformerObstacle bottom, PlatformerObstacle left,
                        PlatformerObstacle right) {
        maxCoords = new double[]{area.x + area.width, area.y + area.height};
        minCoords = new double[]{area.x, area.y};

        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public boolean doCollide(String axis) {
        return true;
    }

    public void handleCollision(String axis, PlatformerPlayer entity) {
        switch (axis) {
            case ("+x"): {
                right.onCollide(axis, entity);
                break;
            }
            case ("-x"): {
                left.onCollide(axis, entity);
                break;
            }
            case ("+y"): {
                bottom.onCollide(axis, entity);
                break;
            }
            case ("-y"): {
                top.onCollide(axis, entity);
                break;
            }
            default: {
                throw new IllegalArgumentException(fmt("Invalid axis: %s", axis));
            }
        }
    }
}
