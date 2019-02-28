package io.github.jgame.math;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

/**
 * Vector2 useful for adding two sets of coordinates such as applying velocities.
 * <p>
 * Implements {@code distanceTo()}, {@code velocityTo()}, and {@code angleTo()}
 */
public class Vector2 {
    public double x;
    public double y;

    /**
     * Create {@code Vector2} containing these coordinates
     *
     * @param x1 X position
     * @param y1 Y position
     */
    public Vector2(double x1, double y1) {
        x = x1;
        y = y1;
    }

    /**
     * Create a copy of another {@code Vector2} object.
     *
     * @param other Vector2 to copy
     */
    public Vector2(Vector2 other) {
        x = other.x;
        y = other.y;
    }

    /**
     * Get the sum of two {@code Vector2}s
     *
     * @param other Vector2 to add to
     * @return Vector2 containing sum
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(other.x + x, other.y + y);
    }

    /**
     * Get the difference of two {@code Vector2}s
     *
     * @param other Vector2 to subtract from this
     * @return difference
     */
    public Vector2 subtract(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    /**
     * Get the coordinates in the form of an integer array.
     *
     * @return {@code new int[]{(int) x, (int) y}}
     */
    public int[] toInt() {
        return new int[]{(int) x, (int) y};
    }

    /**
     * Get the angle from this point to another point.
     *
     * @param other Get the angle to another Vector2
     * @return Angle in the form of degrees
     */
    public double angleTo(Vector2 other) {
        Vector2 delta = other.subtract(this);
        return Math.toDegrees(Math.atan2(delta.y, delta.x)) + 90d;
    }

    /**
     * Get the velocity of an object at this point traveling directly at {@code target} at {@code speed}
     *
     * @param target Vector2 to travel towards
     * @param speed Speed at which to travel
     * @return Vector2 velocity
     */
    public Vector2 velocityTo(Vector2 target, double speed) {
        Vector2 delta = target.subtract(this);
        double travelTime = this.distanceTo(target) / speed;
        Vector2 velocity;
        if (travelTime != 0) {
            velocity = new Vector2((delta.x / travelTime), (delta.y / travelTime));
        } else {
            velocity = new Vector2(0, 0);
        }
        //System.out.println(velocity);
        return velocity;
    }

    /**
     * Get the distance from this point to the {@code target}
     * Gets ∆x and ∆y and plugs them into the pythagorean theorum
     * <ul>
     *     <li>
     *         {@code distance = sqrt(pow(target.x - this.x, 2) + pow(target.y - this.y, 2))}
     *     </li>
     * </ul>
     * @param target Target Vector2
     * @return Distance in the form of a double
     */
    public double distanceTo(Vector2 target) {
        Vector2 delta = target.subtract(this);
        return Math.sqrt(Math.pow(delta.x, 2) + Math.pow(delta.y, 2));
    }

    @Override
    public String toString() {
        return fmt(JGameStr.getString("math.Vector2.toStringFormat"), x, y);
    }
}
