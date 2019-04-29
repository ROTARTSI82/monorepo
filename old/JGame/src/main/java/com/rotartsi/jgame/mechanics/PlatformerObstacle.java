package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Sprite;

/**
 * Not yet implemented...
 * TODO: Implement this
 */
public class PlatformerObstacle extends Sprite {

    public int climbDifficulty = 0;

    /*
    Bounces are very glitchy right now so disable them for now.
     */
    public Vector2[] xBounce = new Vector2[]{new Vector2(0, 0), new Vector2(0, 0)}; // {nxBounce, pxBounce}
    public Vector2[] yBounce = new Vector2[]{new Vector2(0, 0), new Vector2(0, 0)}; // {nyBounce, pyBounce}

    public double friction = 0;
    public double climbSpeed = 0.1;

    public double speed = 0.1;
    public double jumpPower = 5;
    public double gravity = 0.025;
    /**
     * Not yet implemented...
     */
    public PlatformerObstacle() {
        super(Sprite.defaultImage);
        //throw new UnsupportedOperationException(JGameStr.getString("notImplemented"));
    }

    public void onCollide(String axis, PlatformerEntity player) {
        if (axis.equals("-y")) {
            player.speed = speed * player.speedMult;
            player.gravity = gravity * player.gravityMult;
            player.jumpPower = jumpPower * player.jumpMult;
        }
        if (climbDifficulty <= player.climbSkill && (axis.equals("+x") || axis.equals("-x"))) {
            player.climbSpeed = climbSpeed * player.climbSpeedMult;
        }
    }

    public boolean doCollide(String axis) {
        return true;
    }
}
