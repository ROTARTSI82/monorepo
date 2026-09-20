package com.rotartsi.jgame.math;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;

/**
 * Vector2 useful for adding two sets of coordinates such as applying velocities.
 * <p>
 * Implements {@code distanceTo()}, {@code velocityTo()}, and {@code angleTo()}
 */
public class Vector2 {

    public static Vector2 origin = new Vector2(0, 0);

    /**
     * The first double in the the set.
     */
    public double x;

    /**
     * The second double in the set.
     */
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
     * Project a point onto the specified line using a parallel line passing through this point
     * and finding where it intercepts with the specified line.
     * <p>
     * NOTE: If a NaN slope is supplied, the argument y_intercept would be interpreted to be the x_intercept.
     *
     * @param slope       Slope of line
     * @param y_intercept The Y offset of the line
     * @return Point that fits onto the specified line.
     */
    public Vector2 projectOnto(double slope, double y_intercept) {
        if (Double.isNaN(slope)) {
            return new Vector2(y_intercept, this.y);
        }
        if (slope == 0) {
            return new Vector2(this.x, y_intercept);
        }
        double m = -(1d / slope);
        double b = this.y - (m * this.x);
        // this.y = m * this.x + b
        Vector2 ret = new Vector2(0, 0);
        ret.x = (b - y_intercept) / (slope - m);
        ret.y = ret.x * m + b;
        // This should be mathematically impossible. This is just here to test if I did my math correctly.
        if (ret.y != (ret.x * slope + y_intercept)) {
            throw new ArithmeticException(fmt("Failed to calculate intersect: line1=%s, line2=%s",
                    (ret.x * slope + y_intercept), ret.x * m + b));
        }
        //  (b - y_intercept) / (slope - m) =  ret.x
        return ret;
    }

    public Vector2 reflectAround(Vector2 point) {
        Vector2 delta = this.subtract(point);
        return new Vector2(point.x - delta.x, point.y - delta.y);
    }

    public Vector2 dilateRelativeTo(Vector2 point, Vector2 scaleFactor) {
        Vector2 delta = this.subtract(point);
        return new Vector2(point.x + (delta.x * scaleFactor.x), point.y + (delta.y * scaleFactor.y));
    }

    public Vector2 rotateAround(Vector2 point, double rotation) {
        double distance = point.distanceTo(this);
        return positionFromDegrees(rotation, distance);
    }

    public static double degreesToSlope(double degrees) {
        return Math.tan(Math.toRadians(degrees));
    }

    public static double slopeToDegrees(double slope) {
        return Math.toDegrees(Math.atan2(slope, 1));
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

    public Vector2 multiply(Vector2 other) {
        return new Vector2(x * other.x, y * other.x);
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
        return Math.toDegrees(Math.atan2(delta.y, delta.x));
    }

//    public static void main(String[] args) {
//        Vector2 origin = new Vector2(0, 0);
//        double size = 10;
//        System.out.println(origin.angleTo(new Vector2(size, 0)));
//        System.out.println(origin.angleTo(new Vector2(size, size)));
//        System.out.println(origin.angleTo(new Vector2(0, size)));
//        System.out.println(origin.angleTo(new Vector2(-size, size)));
//        System.out.println(origin.angleTo(new Vector2(-size, 0)));
//        System.out.println(origin.angleTo(new Vector2(-size, -size)));
//        System.out.println(origin.angleTo(new Vector2(0, -size)));
//        System.out.println(origin.angleTo(new Vector2(size, - size)));
//    }

    /**
     * Get the position of {@code length} units forward at an angle {@code angle} degrees from this point.
     *
     * @param angle  Angle
     * @param length length
     * @return position
     */
    public Vector2 positionFromDegrees(double angle, double length) {
        angle = Math.toRadians(angle);
        return new Vector2(x + Math.cos(angle) * length, y + Math.sin(angle) * length);
    }

    /**
     * Get the velocity if we want to travel at {@code angle} degrees at {@code speed}
     *
     * @param angle Angle
     * @param speed Speed
     * @return Velocity
     */
    public Vector2 velocityFromDegrees(double angle, double speed) {
        return velocityTo(positionFromDegrees(angle, 1), speed);
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
        if (travelTime != 0) {
            return new Vector2((delta.x / travelTime), (delta.y / travelTime));
        } else {
            return new Vector2(0, 0);
        }
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
        return Math.hypot(delta.x, delta.y);
    }

    /**
     * toString() for pretty printing.
     *
     * @return "Vector2[x, y]"
     */
    @Override
    public String toString() {
        return fmt(JGameStr.getString("math.Vector2.toStringFormat"), x, y);
    }
}
