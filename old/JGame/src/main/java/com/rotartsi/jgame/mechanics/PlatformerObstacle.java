package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Sprite;

import java.util.LinkedList;

/**
 * Not yet implemented...
 * TODO: Implement this
 */
public class PlatformerObstacle extends Sprite implements PlatformerEntity {

    public int climbDifficulty = 1;

    /*
    Bounces are very glitchy right now so disable them for now.
     */
    public Vector2 bounce = new Vector2(-5 * PlatformerPlayer.multSqr, -5 * PlatformerPlayer.multSqr);
    public double wallJumpPower = 2.5 * PlatformerPlayer.multSqr;


    public double friction = 0;
    public double climbSpeed = 0.1 * PlatformerPlayer.multConst;

    public double speed = 0.1 * PlatformerPlayer.multConst;
    public double jumpPower = 0.1 * PlatformerPlayer.multConst;
    public double gravity = 0.025 * PlatformerPlayer.multConst;

    public boolean doBounce = true;
    /**
     * Not yet implemented...
     */
    public PlatformerObstacle() {
        super(Sprite.defaultImage);
        //throw new UnsupportedOperationException(JGameStr.getString("notImplemented"));
    }

    public void onCollide(String axis, PlatformerPlayer player) {
        if (axis.equals("-y")) {
            player.speed = speed * player.speedMult;
            player.gravity = gravity * player.gravityMult;
            player.jumpPower = jumpPower * player.jumpMult;
        }
        if (!(climbDifficulty <= player.climbSkill)) {
            return;
        }
        if (axis.equals("+x") || axis.equals("-x")) {
            player.climbSpeed = climbSpeed * player.climbSpeedMult;
        }
//        System.out.println(axis);

        if (!doBounce) {
            return;
        }

        switch (axis) {
            case "-x": {
                if (player.internalState.get("jump") && player.vel.y > 0) {
                    player.vel.x -= bounce.x;
                    player.vel.y -= wallJumpPower;
                    player.clampVelocity();
                }
                break;
            }
            case "+x": {
                if (player.internalState.get("jump") && player.vel.y > 0) {
                    player.vel.x += bounce.x;
                    player.vel.y -= wallJumpPower;
                    player.clampVelocity();
                }
                break;
            }
        }
    }

    public boolean doCollide(String axis) {
        return true;
    }

    @Override
    public void damage(double amount) {
        System.out.println("damage amount = " + amount);
    }

    @Override
    public void update() {

    }

    @Override
    public void onCollide(LinkedList<Sprite> collisions, String axis) {

    }
}
