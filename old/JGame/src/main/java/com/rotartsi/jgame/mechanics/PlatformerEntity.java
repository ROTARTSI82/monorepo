package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.event.AnyEvent;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.sprite.Sprite;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.jgame.util.SettingsBundle;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Not yet implemented...
 * TODO: Implement this
 */
public class PlatformerEntity extends Sprite {
    public static double multSqr = 250;
    public static double multConst = multSqr * multSqr;
    public double health = 0;
    public long lastUpdate = System.currentTimeMillis();
    public double framerateSpeedMultiplier = 1;

    public double speed = 5 * multConst;
    public double climbSpeed = 5 * multConst;

    public double speedMult = 1;
    public double gravityMult = 1;
    public double jumpMult = 1;
    public double airFriction = 0.025 * multConst;

    public double jumpPower = 0;
    public double gravity = 0;

    public int climbSkill = 1;

    public Vector2[] terminalVelocity = new Vector2[]{new Vector2(10 * multConst, 10 * multConst), new Vector2(-10 * multConst, -10 * multConst)};

    public SettingsBundle keybinds;

    protected HashMap<String, Boolean> internalState = new HashMap<>();

    public Group collidables;

    protected ScreenBounds bounds;

    public double frictionMult = 1;
    public double climbSpeedMult = 1;

    /**
     * Not yet implemented...
     *
     * @param img NOT IMPLEMENTED
     * @param bounds   The boundaries of the screen.
     */
    public PlatformerEntity(BufferedImage img, ScreenBounds bounds) {
        super(img);

        internalState.put("forward", false);
        internalState.put("backward", false);
        internalState.put("jump", false);
        internalState.put("climb", false);
        internalState.put("grounded", false);
        //internalState.put("firing", false);
        this.bounds = bounds;
    }

    public void damage(double amount) {
        health -= amount;
    }

    public void setBounds(ScreenBounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        framerateSpeedMultiplier = (now - lastUpdate) / 1000d;
//        System.out.println("framerateSpeedMultiplier = " + framerateSpeedMultiplier + ", 1/60=" + 1d/60d);
        internalState.put("grounded", false);
        internalState.put("climb", false);
        checkYCollisions();
        checkBounds();
        updateInputState();
        if (!internalState.get("grounded")) {
            vel.y += gravity * framerateSpeedMultiplier;
        }
        if (vel.x > 0) {
            vel.x -= airFriction * framerateSpeedMultiplier;
        } else if (vel.x < 0) {
            vel.x += airFriction * framerateSpeedMultiplier;
        }
        clampVelocity();
        pos.y = pos.y + (vel.y * framerateSpeedMultiplier);
        updateRect();
        lastUpdate = now;
    }

    protected void checkBounds() {
        if (absPos.x < bounds.minCoords[0] && bounds.doCollide("-x")) { // - x
            internalState.put("climb", bounds.left.climbDifficulty <= climbSkill);
            pos.x = bounds.minCoords[0] + (size.x / 2);
            vel.x = 0;
            bounds.handleCollision("-x", this);
            //vel = vel.multiply(bounds.left.xBounce[1]).multiply(xBounceMult[1]);
        }
        if (absPos.y < bounds.minCoords[1] && bounds.doCollide("-y")) { // - y
            vel.y = 0;
            pos.y = bounds.minCoords[1] + (size.y / 2);
            bounds.handleCollision("-y", this);
            if (vel.x > 0) {
                vel.x -= bounds.bottom.friction * frictionMult * framerateSpeedMultiplier;
            } else if (vel.x < 0) {
                vel.x += bounds.bottom.friction * frictionMult * framerateSpeedMultiplier;
            }
        }
        if (pos.x + size.x / 2 > bounds.maxCoords[0] && bounds.doCollide("+x")) { // + x
            pos.x = bounds.maxCoords[0] - (size.x / 2);
            internalState.put("climb", bounds.right.climbDifficulty <= climbSkill);
            vel.x = 0;
            bounds.handleCollision("+x", this);
            //vel = vel.multiply(bounds.right.xBounce[1]).multiply(xBounceMult[1]);
        }
        if (pos.y + size.y / 2 > bounds.maxCoords[1] && bounds.doCollide("+y")) { // + y
            pos.y = bounds.maxCoords[1] - (size.y / 2);
            internalState.put("grounded", true);
            vel.y = 0;
            bounds.handleCollision("+y", this);
        }
        updateRect();
    }

    protected void clampVelocity() {
        vel.y = Math.max(terminalVelocity[1].y, Math.min(vel.y, terminalVelocity[0].y));
        vel.x = Math.max(terminalVelocity[1].x, Math.min(vel.x, terminalVelocity[0].x));
    }

    public void updateEvent(AnyEvent event) {
        if (event.matchesString(keybinds.get("keybinds.fire.activate",
                "id:mouse.pressed,button:1"))) {
            // internalState.put("firing", true);
        }
        if (event.matchesString(keybinds.get("keybinds.fire.deactivate",
                "id:mouse.released,button:1"))) {
            // internalState.put("firing", false);
        }
        if (event.matchesString(keybinds.get("keybinds.forward.activate",
                "id:key.pressed,keyCode:68"))) {
            internalState.put("forward", true);
        }
        if (event.matchesString(keybinds.get("keybinds.forward.deactivate",
                "id:key.released,keyCode:68"))) {
            internalState.put("forward", false);
        }
        if (event.matchesString(keybinds.get("keybinds.backward.activate",
                "id:key.pressed,keyCode:65"))) {
            internalState.put("backward", true);
        }
        if (event.matchesString(keybinds.get("keybinds.backward.deactivate",
                "id:key.released,keyCode:65"))) {
            internalState.put("backward", false);
        }
        if (event.matchesString(keybinds.get("keybinds.jump.activate",
                "id:key.pressed,keyCode:32"))) {
            internalState.put("jump", true);
        }
        if (event.matchesString(keybinds.get("keybinds.jump.deactivate",
                "id:key.released,keyCode:32"))) {
            internalState.put("jump", false);
        }
    }

    public void updateInputState() {
        if (internalState.get("forward")) {
            vel.x += speed * framerateSpeedMultiplier;
        }
        if (internalState.get("backward")) {
            vel.x -= speed * framerateSpeedMultiplier;
        }
        if (internalState.get("jump") && internalState.get("grounded")) {
            vel.y -= jumpPower;
        }

        if (vel.x < 0) {
            clampVelocity();
            pos.x += vel.x * framerateSpeedMultiplier;
            updateRect();
            checkNXCollisions();
            // checkBounds();
        } else if (vel.x > 0) {
            clampVelocity();
            pos.x += vel.x * framerateSpeedMultiplier;
            updateRect();
            checkPXCollisions();
            // checkBounds();
        }
//        System.out.println("pos = " + pos);

        if (internalState.get("climb")) {
            vel.y -= climbSpeed * framerateSpeedMultiplier;
        }
    }

    void checkPXCollisions() {
        LinkedList<Sprite> cols = collidesWith(collidables);
        if (cols.size() > 0) {
            onCollide(cols, "+x");
            Sprite c = cols.get(0);
            if (c instanceof PlatformerObstacle) {
                PlatformerObstacle col = (PlatformerObstacle) c;
                if (!col.doCollide("+x")) {
                    return;
                }
                internalState.put("climb", (col.climbDifficulty <= climbSkill));
                col.updateRect();
                pos.x = col.absPos.x - (size.x / 2d);
                updateRect();
                vel.x = 0;
                col.onCollide("+x", this);
//                vel = vel.multiply(col.xBounce[0]).multiply(xBounceMult[0]);
            }
        }
    }

    public void onCollide(LinkedList<Sprite> collisions, String axis) {

    }

    void checkNXCollisions() {
        LinkedList<Sprite> cols = collidesWith(collidables);
        if (cols.size() > 0) {
            onCollide(cols, "-x");
            Sprite c = cols.get(0);
            if (c instanceof PlatformerObstacle) {
                PlatformerObstacle col = (PlatformerObstacle) c;
                if (!col.doCollide("-x")) {
                    return;
                }
                internalState.put("climb", (col.climbDifficulty <= climbSkill));
                pos.x = (col.pos.x + (col.size.x / 2)) + (size.x / 2d);
                updateRect();
                vel.x = 0;
                col.onCollide("-x", this);
//                vel = vel.multiply(col.xBounce[0]).multiply(xBounceMult[0]);
            }
        }
    }

    protected void checkYCollisions() {
        if (vel.y > 0) {
            LinkedList<Sprite> cols = collidesWith(collidables);
            if (cols.size() > 0) {
                onCollide(cols, "+y");
                Sprite c = cols.get(0);
                if (c instanceof PlatformerObstacle) {
                    PlatformerObstacle col = (PlatformerObstacle) c;
                    if (!col.doCollide("+y")) {
                        return;
                    }
                    pos.y = col.absPos.y - (size.y / 2d);
                    updateRect();
                    internalState.put("grounded", true);
                    vel.y = 0;
                    col.onCollide("+y", this);
//                    vel = vel.multiply(col.yBounce[1]).multiply(yBounceMult[1]);
                    if (vel.x > 0) {
                        vel.x -= col.friction * frictionMult * framerateSpeedMultiplier;
                    } else if (vel.x < 0) {
                        vel.x += col.friction * frictionMult * framerateSpeedMultiplier;
                    }
                }
            }
        } else if (vel.y < 0) {
            LinkedList<Sprite> cols = collidesWith(collidables);
            if (cols.size() > 0) {
                onCollide(cols, "-y");
                Sprite c = cols.get(0);
                if (c instanceof PlatformerObstacle) {
                    PlatformerObstacle col = (PlatformerObstacle) c;
                    if (!col.doCollide("-y")) {
                        return;
                    }
                    col.onCollide("-y", this);
                    pos.y = (col.pos.y + (col.size.y / 2)) + (size.y / 2d);
                    updateRect();
                    vel.y = 0;
//                    vel = vel.multiply(col.yBounce[0]).multiply(yBounceMult[0]);
                }
            }
        }
    }
}
