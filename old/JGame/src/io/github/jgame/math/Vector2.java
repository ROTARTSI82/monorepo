package io.github.jgame.math;

public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x1, double y1) {
        x = x1;
        y = y1;
    }

    public Vector2(Vector2 other) {
        x = other.x;
        y = other.y;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(other.x + x, other.y + y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    public int[] toInt() {
        return new int[]{(int) x, (int) y};
    }

    public double angleTo(Vector2 other) {
        Vector2 delta = other.subtract(this);
        return Math.toDegrees(Math.atan2(delta.y, delta.x)) - 90d;
    }

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

    public double distanceTo(Vector2 target) {
        Vector2 delta = target.subtract(this);
        return Math.sqrt(Math.pow(delta.x, 2) + Math.pow(delta.y, 2));
    }

    @Override
    public String toString() {
        return String.format("Vector2[%s, %s]", x, y);
    }
}
