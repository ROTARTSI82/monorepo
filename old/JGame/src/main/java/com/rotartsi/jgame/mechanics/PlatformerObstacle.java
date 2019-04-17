package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Sprite;

/**
 * Not yet implemented...
 * TODO: Implement this
 */
public class PlatformerObstacle extends Sprite {

    public int climbDifficulty;

    public Vector2[] xBounce = new Vector2[]{new Vector2(3, 3), new Vector2(3, 3)}; // {nxBounce, pxBounce}
    public Vector2[] yBounce = new Vector2[]{new Vector2(3, 3), new Vector2(3, 3)}; // {nyBounce, pyBounce}

    public double friction = 1;
    public double climbSpeed = 5;

    public double speed = 5;
    public double jumpPower = 10;
    public double gravity = 0.5;
    /**
     * Not yet implemented...
     */
    public PlatformerObstacle() {
        super(Sprite.defaultImage);
        //throw new UnsupportedOperationException(JGameStr.getString("notImplemented"));
    }

    public void onCollide(String axis, PlatformerEntity player) {
        player.speed = speed * player.speedMult;
        player.gravity = gravity * player.gravityMult;
        player.jumpPower = jumpPower * player.jumpMult;
    }

    public boolean doCollide(String axis) {
        return true;
    }
}
