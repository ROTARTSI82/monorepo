package io.github.jgame.math;

import java.awt.*;
import java.util.LinkedList;

import static io.github.jgame.Constants.JGameStr;

public class PolygonCollide {
    private static final Vector2 origin = new Vector2(0, 0);
    public Vector2 center;
    private double[][] untransformed;
    private double[][] verts;

    public PolygonCollide(double[][] points) {
        verts = points;
        getCenter();
        saveVerts();
        /*
        int[] xpoints = new int[points.length];
        int[] ypoints = new int[points.length];
        int i = 0;
        for (double[] point : points) {
            xpoints[i] = (int) point[0];
            ypoints[i] = (int) point[1];
            i++;
        }
        polygon = new Polygon(xpoints, ypoints, points.length);
        */
    }

    public PolygonCollide(Rectangle rect) {
        verts = new double[][]{{rect.x, rect.y}, {rect.x + rect.width, rect.y},
                {rect.x + rect.width, rect.y + rect.height}, {rect.x, rect.y + rect.height}};
        getCenter();
        saveVerts();
    }

    public PolygonCollide(Polygon poly) {
        // polygon = poly;
        verts = new double[poly.npoints][2];

        int i = 0;
        for (double x : poly.xpoints) {
            if (i >= poly.npoints) {
                break;
            }
            verts[i][0] = x;
            i++;
        }
        i = 0;
        for (double y : poly.ypoints) {
            if (i >= poly.npoints) {
                break;
            }
            verts[i][1] = y;
            i++;
        }
        getCenter();
        saveVerts();
    }

    public void moveTo(Vector2 newCenter) {
        getCenter();
        double[] delta = new double[]{newCenter.x - center.x, newCenter.y - center.y};
        for (int i = 0; i < verts.length; i++) {
            verts[i][0] = verts[i][0] + delta[0];
            verts[i][1] = verts[i][1] + delta[1];
        }
        center = new Vector2(newCenter);
    }

    public void scaleTo(double scale) {
        throw new UnsupportedOperationException(JGameStr.getString("notImplemented"));
        /*
        for (int i = 0; i < verts.length; i++) {
            Vector2 point = new Vector2(verts[i][0], verts[i][1]);
            Vector2 delta = point.subtract(center);
            double angle = Math.atan2(delta.y, delta.x);
            double hypot = Math.sqrt(Math.pow(delta.x, 2) + Math.pow(delta.y, 2)) * scale;
            delta.y = Math.sin(angle) * hypot;
            delta.x = Math.cos(angle) * hypot;
            verts[i][0] = verts[i][0] + delta.x;
            verts[i][1] = verts[i][1] + delta.y;
        }
        System.out.println(Arrays.deepToString(verts));
        getCenter();
        */
    }

    public void rotate(double degrees) {
        throw new UnsupportedOperationException(JGameStr.getString("notImplemented"));
        /*
        for (int i = 0; i < verts.length; i++) {
            Vector2 pointVector = new Vector2(verts[i][0], verts[i][1]);
            Vector2 delta = pointVector.subtract(center);
            double hypot = Math.sqrt(Math.pow(delta.x, 2) + Math.pow(delta.y, 2));
            double angle = Math.atan2(delta.y, delta.x) - Math.toRadians(degrees);
            delta.y = Math.sin(angle) * hypot;
            delta.x = Math.cos(angle) * hypot;
            verts[i][0] = verts[i][0] + delta.x;
            verts[i][1] = verts[i][1] + delta.y;
        }
        System.out.println(Arrays.deepToString(verts));
        getCenter();
        */
    }

    /**
     * Restore the PolygonCollide to it's unrotated, unscaled, and untranslated self.
     */
    public void restore() {
        verts = new double[untransformed.length][2];
        for (int i = 0; i < untransformed.length; i++) {
            verts[i] = new double[]{untransformed[i][0], untransformed[i][1]};
        }
    }

    public void getCenter() {
        if (verts.length == 0) {
            return;
        }
        double[] ret = new double[]{verts[0][0], verts[0][1]};
        for (int i = 1; i < verts.length; i++) { // Skip the first element, as it's already accounted for.
            ret[0] = (ret[0] + verts[i][0]) / 2;
            ret[1] = (ret[1] + verts[i][1]) / 2;
        }
        center = new Vector2(ret[0], ret[1]);
    }

    public Polygon getPolygon() {
        int[] xpoints = new int[verts.length];
        int[] ypoints = new int[verts.length];
        int i = 0;
        for (double[] point : verts) {
            xpoints[i] = (int) point[0];
            ypoints[i] = (int) point[1];
            i++;
        }
        return new Polygon(xpoints, ypoints, verts.length);
    }

    private void saveVerts() {
        untransformed = new double[verts.length][2];
        for (int i = 0; i < verts.length; i++) {
            untransformed[i] = new double[]{verts[i][0], verts[i][1]};
        }
    }

    public LinkedList<Double> getAxes() {
        double[] prevPoint = verts[verts.length - 1];
        LinkedList<Double> slopes = new LinkedList<>();

        for (double[] point : verts) {
            double slope;
            if (prevPoint[1] - point[1] == 0) {
                if (!slopes.contains(0d)) {
                    slopes.add(0d);
                    continue;
                }
            }
            if (prevPoint[0] - point[0] != 0) {
                slope = prevPoint[1] - point[1] / prevPoint[0] - point[0];
            } else {
                if (!slopes.contains((double) Integer.MIN_VALUE)) {
                    slopes.add((double) Integer.MIN_VALUE);
                }
                continue;
            }
            if (!slopes.contains(slope)) {
                slopes.add(slope);
            }
            prevPoint = point;
        }
        return slopes;
    }

    /**
     * Get the projected point on a axis by using a perpendicular line and calculating the intercepts.
     *
     * @param point     double[x, y]
     * @param axisSlope double (slope of axis to project onto)
     * @return double[x, y] of projected point
     */
    public double projectPoint(double[] point, double axisSlope) {
        if (axisSlope == 0) {
            return Math.abs(point[0]);
        }
        if (axisSlope == (double) Integer.MIN_VALUE) {
            return Math.abs(point[1]);
        }

        Vector2 ret = new Vector2(0, 0);

        double projectSlope = -(1 / axisSlope);
        double b = point[1] - projectSlope * point[0];
        ret.x = b / (axisSlope - projectSlope);
        ret.y = projectSlope * ret.x + b;
        return origin.distanceTo(ret);
    }

    private boolean intersectsOnOwnAxis(PolygonCollide other) {
        LinkedList<Double> axes = getAxes();
        for (double slope : axes) {
            double myMax = Integer.MIN_VALUE;
            double myMin = Integer.MIN_VALUE;

            double otherMax = Integer.MIN_VALUE;
            double otherMin = Integer.MIN_VALUE;

            for (double[] point : verts) {
                double distance = projectPoint(point, slope);
                if (distance > myMax || myMax == Integer.MIN_VALUE) {
                    myMax = distance;
                }
                if (distance < myMin || myMin == Integer.MIN_VALUE) {
                    myMin = distance;
                }
            }

            for (double[] point : other.verts) {
                double distance = projectPoint(point, slope);
                if (distance > otherMax || otherMax == Integer.MIN_VALUE) {
                    otherMax = distance;
                }
                if (distance < otherMin || otherMin == Integer.MIN_VALUE) {
                    otherMin = distance;
                }
            }
            //System.out.format("mmin: %s, mmax: %s, omin: %s, omax: %s\n", myMin, myMax, otherMin, otherMax);

            if ((otherMax >= myMin && otherMax <= myMax)) { //|| (otherMax <= myMin && otherMax >= myMax)) {
                continue;
            }
            if ((otherMin >= myMin && otherMin <= myMax)) { //|| (otherMin <= myMin && otherMin >= myMax)) {
                continue;
            }
            if ((myMin >= otherMin && myMin <= otherMax)) { //|| (myMin <= otherMin && myMin >= otherMax)) {
                continue;
            }
            if ((myMax >= otherMin && myMax <= otherMax)) { //|| (myMax <= otherMin && myMax >= otherMax)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public boolean intersects(PolygonCollide other) {
        return other.intersectsOnOwnAxis(this) && this.intersectsOnOwnAxis(other);
    }

    public boolean intersects(Rectangle rect) {
        return intersects(new PolygonCollide(rect));
    }
}
