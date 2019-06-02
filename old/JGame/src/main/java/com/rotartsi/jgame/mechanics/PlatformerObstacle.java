package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Sprite;

/**
 * Not yet implemented...
 * TODO: Implement this
 */
public class PlatformerObstacle extends PlatformerEntity {

    public int climbDifficulty = 1;

    /*
    Bounces are very glitchy right now so disable them for now.
     */
    public Vector2 bounce = new Vector2(-5, -5);
    public double wallJumpPower = 2.5;

    public double friction = 0;
    public double climbSpeed = 0.1;

    public double speed = 0.1;
    public double jumpPower = 5;
    public double gravity = 0.025;

    public boolean doBounce = true;
    /**
     * Not yet implemented...
     */
    public PlatformerObstacle() {
        super(Sprite.defaultImage, null);
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
//        System.out.println(axis);

        if (!doBounce) {
            return;
        }

        switch (axis) {
            case "-x": {
                player.vel.x -= bounce.x;
                player.vel.y -= wallJumpPower;
                player.clampVelocity();
                break;
            }
            case "+x": {
                player.vel.x += bounce.x;
                player.vel.y -= wallJumpPower;
                player.clampVelocity();
                break;
            }
        }
    }

    public boolean doCollide(String axis) {
        return true;
    }
}
