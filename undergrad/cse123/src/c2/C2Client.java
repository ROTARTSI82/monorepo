/**
 * @author Grant Yang
 * @version 2025.11.01
 * CSE 123 BC
 * C2: Mondrian Art
 * TA: Benoit Le
 */

import java.awt.*;

/**
 * Tests the divideCanvas() method by running two subdivisions
 * on a 400x400 test image, saving it to "test.png", and displaying it.
 */
private static void testDivideCanvas() {
    Picture pic1 = new Picture(400, 400);
    Color[][] pix = pic1.getPixels();
    divideCanvas(pix, 2);
    pic1.setPixels(pix);
    pic1.save("test.png");
    pic1.show();
}

/**
 * Entry point. Prompts the user to generate either a
 * basic or complex Mondrian painting of a specified size, then saves and displays it.
 */
void main() {
    // testDivideCanvas()

    Scanner console = new Scanner(System.in);
    System.out.println("Welcome to the CSE 123 Mondrian Art Generator!");

    int choice = 0;
    while (choice != 1 && choice != 2) {
        System.out.print("Enter 1 for a basic Mondrian or 2 for a complex Mondrian: ");
        choice = console.nextInt();
    }
    System.out.print("Enter image width (>= 300px): ");
    int width = console.nextInt();
    System.out.print("Enter image height (>= 300px): ");
    int height = console.nextInt();

    Mondrian mond = new Mondrian();
    Picture pic = new Picture(width, height);
    Color[][] pixels = pic.getPixels();

    if (choice == 1) {
        mond.paintBasicMondrian(pixels);
    } else {
        mond.paintComplexMondrian(pixels);
    }

    pic.setPixels(pixels);
    pic.save(choice == 1 ? "basic.png" : "extension.png");
    pic.show();
    System.out.println("Enjoy your artwork!");
}

/**
 * Fills a rectangular region of a canvas with white.
 * @param pixels The canvas of pixels to fill, in row-major order.
 * @param x1 The 0-indexed left boundary of the area (inclusive).
 *           We require that 0 <= x1 < canvas width
 * @param x2 The 0-indexed right boundary of the area (exclusive).
 *           We require that x1 < x2 <= canvas width
 * @param y1 The 0-indexed top boundary of the area (inclusive).
 *           We require that 0 <= y1 < canvas height
 * @param y2 The 0-indexed bottom boundary of the area (exclusive).
 *           We require that y1 < y2 <= canvas height
 * @throws IndexOutOfBoundsException If any of the bounds lie outside the canvas.
 */
public static void fill(Color[][] pixels, int x1, int x2, int y1, int y2) {
    for (int x = x1; x < x2; x++) {
        for (int y = y1; y < y2; y++) {
            pixels[y][x] = Color.WHITE;
        }
    }
}

/**
 * Recursively divides a region of the canvas into four equal sub-quadrants,
 * leaving a 1-pixel border between each subregion.
 * The base case fills the region with white.
 * @param pixels The canvas of pixels to operate on, in row-major order.
 * @param left The 0-indexed left boundary of the area (inclusive).
 *             We require that 0 <= left < canvas width
 * @param right The 0-indexed right boundary of the area (exclusive).
 *              We require that left < right <= canvas width
 * @param top The 0-indexed top boundary of the area (inclusive).
 *            We require that 0 <= top < canvas height
 * @param bottom The 0-indexed bottom boundary of the area (exclusive).
 *               We require that top < bottom <= canvas height
 * @param depth The number of remaining recursive divisions to perform.
 */
private static void divideHelper(Color[][] pixels, int left, int right,
                                 int top, int bottom, int depth) {
    if (depth > 0) {
        int midX = left + (right - left) / 2;
        int midY = top + (bottom - top) / 2;
        divideHelper(pixels, left, midX, top, midY, depth - 1);
        divideHelper(pixels, midX + 1, right, top, midY, depth - 1);
        divideHelper(pixels, left, midX, midY + 1, bottom, depth - 1);
        divideHelper(pixels, midX + 1, right, midY + 1, bottom, depth - 1);
    } else {
        fill(pixels, left, right, top, bottom);
    }
}

/**
 * Divides the given canvas into equal regions along both x and y and fills them with white,
 * leaving a 1-pixel border both between regions and around the entire canvas.
 * @param pixels The canvas to paint on. Must be large enough to accommodate the
 *               requested number of divisions.
 * @param n The number of divisions into quadrants to perform,
 *          so that we end up with 4^n regions total. Must be >= 0.
 */
public static void divideCanvas(Color[][] pixels, int n) {
    divideHelper(pixels, 1, pixels[0].length - 1, 1, pixels.length - 1, n);
}
