/**
 * @author Grant Yang
 * @version 2025.11.01
 * CSE 123 BC
 * C2: Mondrian Art
 * TA: Benoit Le
 */

import java.util.*;
import java.awt.*;

/**
 *
 */
public class Mondrian {

    private static final Random RAND = new Random();
    private static final Color[] PALETTE =
            new Color[]{Color.RED, Color.YELLOW, Color.CYAN, Color.WHITE};

    private int thresholdW;
    private int thresholdH;
    private int minSize;

    /**
     * Fills a rectangular patch of a canvas with a random Piet Mondrian-inspired pattern.
     * If the rectangular region is smaller than thresholdW by thresholdH pixels,
     * we fill it with a random color from PALETTE. Otherwise, we randomly
     * divide our region into further subregions (making sure none are smaller than
     * minSize by minSize pixels) until we get regions smaller than thresholdW by thresholdH,
     * and we randomly color each region with a color from PALETTE. A 1-pixel border
     * is left between all regions.
     * @param pix The canvas of pixels to fill. The canvas should be in row-major order.
     * @param left The 0-indexed left boundary of the area (inclusive).
     *             We require that 0 <= left < canvas width
     * @param right The 0-indexed right boundary of the area (exclusive).
     *              We require that left < right <= canvas width
     * @param top The 0-indexed top boundary of the area (inclusive).
     *            We require that 0 <= top < canvas height
     * @param bottom The 0-indexed bottom boundary of the area (exclusive).
     *               We require that top < bottom <= canvas height
     * Violating any precondition above is undefined behavior.
     */
    private void basicMondrian(Color[][] pix, int left, int right, int top, int bottom) {
        int w = right - left;
        int h = bottom - top;

        // the `Math.max`s are just so we never pass in a negative number for the
        // cases where w or h are too small, as in those cases we won't use divX or divY
        int divX = left + minSize + 1 + RAND.nextInt(Math.max(w - 2 * minSize, 1));
        int divY = top + minSize + 1 + RAND.nextInt(Math.max(h - 2 * minSize, 1));

        if (w >= thresholdW && h >= thresholdH) {
            basicMondrian(pix, left, divX, top, divY);
            basicMondrian(pix, divX + 1, right, top, divY);
            basicMondrian(pix, left, divX, divY + 1, bottom);
            basicMondrian(pix, divX + 1, right, divY + 1, bottom);
        } else if (w >= thresholdW) {
            basicMondrian(pix, left, divX, top, bottom);
            basicMondrian(pix, divX + 1, right, top, bottom);
        } else if (h >= thresholdH) {
            basicMondrian(pix, left, right, top, divY);
            basicMondrian(pix, left, right, divY + 1, bottom);
        } else {
            Color fill = PALETTE[RAND.nextInt(PALETTE.length)];
            for (int x = left; x < right; x++)
                for (int y = top; y < bottom; y++)
                    pix[y][x] = fill;
        }
    }

    /**
     * Fills a canvas with a Piet Mondrian-inspired pattern. The canvas is randomly
     * bisected into rectangular regions such that no region is larger than a fourth
     * of the total canvas size in either dimension, and regions are randomly colored
     * red, yellow, cyan, or white. A 1-pixel border is left around the entire canvas
     * and between each region, and no region is smaller than 10 pixels by 10 pixels.
     * @param pixels The canvas to paint on. Must be stored in row-major order, and
     *               must be at least 300 pixels by 300 pixels.
     * @throws IllegalArgumentException If the canvas is null or smaller than 300x300 pixels.
     */
    public void paintBasicMondrian(Color[][] pixels) {
        if (pixels == null || pixels.length < 300 || pixels[0].length < 300)
            throw new IllegalArgumentException(
                    "pixels must not be null and must be at least 300 x 300");

        thresholdW = pixels[0].length / 4;
        thresholdH = pixels.length / 4;
        minSize = 10;
        basicMondrian(pixels, 1, pixels[0].length - 1, 1, pixels.length - 1);
    }

    /**
     * Helper method for painting a rough Sierpinski's carpet pattern made of
     * Piet Mondrian-inspired rectangles.
     * @param pix The canvas to paint on. Must be in row-major order.
     * @param x
     * @param y
     * @param w
     * @param h
     */
    private void mondrianCarpet(Color[][] pix, int x, int y, int w, int h) {
        if (w > 10 && h > 10) {
            thresholdW = Math.max(w / 4, 10);
            thresholdH = Math.max(h / 4, 10);
            minSize = 5;

            basicMondrian(pix, x - w / 2, x + w / 2, y - h / 2, y + h / 2);

            for (int px = -1; px < 2; px++)
                for (int py = -1; py < 2; py++)
                    if (px != 0 || py != 0) {
                        int nx = x + px * w + (RAND.nextBoolean() ? 1 : -1) * RAND.nextInt(4);
                        int ny = y + py * w + (RAND.nextBoolean() ? 1 : -1) * RAND.nextInt(4);
                        nx = Math.clamp(nx, w / 6, pix[0].length - w / 6);
                        ny = Math.clamp(ny, h / 6, pix.length - h / 6);
                        mondrianCarpet(pix, nx, ny, w / 3, h / 3);
                    }
        }
    }


    /**
     *
     * @param pixels The canvas to paint on, stored in row-major order and no smaller than
     *               300 pixels by 300 pixels.
     * @throws IllegalArgumentException If pixels is null or is smaller than 300x300 pixels
     */
    public void paintComplexMondrian(Color[][] pixels) {
        if (pixels == null || pixels.length < 300 || pixels[0].length < 300)
            throw new IllegalArgumentException(
                    "pixels must not be null and must be at least 300 x 300");

        mondrianCarpet(pixels, pixels[0].length / 2, pixels.length / 2,
                pixels[0].length / 3, pixels.length / 3);
    }
}
