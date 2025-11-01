
import java.awt.*;
import java.util.Scanner;

public class C2Client {

    /**
     * Tests the divideCanvas() method by running two subdivisions
     * on a 400x400 test image and saving it to "test.png".
     */
    private static void testDivideCanvas() {
        Picture pic1 = new Picture(400, 400);
        Color[][] pix = pic1.getPixels();
        divideCanvas(pix, 2);
        pic1.setPixels(pix);
        pic1.save("test.png");
        pic1.show();
    }

    public static void main(String[] args) {
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
        } else {    // choice == 2
            mond.paintComplexMondrian(pixels);
        }

        pic.setPixels(pixels);
        pic.save(choice == 1 ? "basic.png" : "extension.png");
        pic.show();
        System.out.println("Enjoy your artwork!");
    }

    /*Fills in pixels within the region defined by x1, x2, y1, and y2 where
    (x1, y1) represents the upper-left corner inclusive, and (x2, y2)
    represents the lower-right corner exclusive. Your solution should leave a single pixel
    as a border around the edges of the region (you can optionally ignore this border requirement).
    We recommend you approach this method iteratively*/
    public static void fill(Color[][] pixels, int x1, int x2, int y1, int y2) {
        for (int x = x1; x < x2; x++)
            for (int y = y1; y < y2; y++)
                pixels[y][x] = Color.WHITE;
    }

    private static void divideHelper(Color[][] pixels, int left, int right,
                                     int top, int bottom, int depth) {
        if (depth > 0) {
            int midX  = left + (right - left) / 2;
            int midY  = top + (bottom - top) / 2;
            divideHelper(pixels, left, midX, midY + 1, bottom, depth - 1);
            divideHelper(pixels, midX + 1, right, midY + 1, bottom, depth - 1);
            divideHelper(pixels, left, midX, top, midY, depth - 1);
            divideHelper(pixels, midX + 1, right, top, midY, depth - 1);
        } else {
            fill(pixels, left, right, top, bottom);
        }
    }

    /*    Divides the given pixels (assumed to be all black) into 4 equally sized regions n times,
     filling in each of the split regions with the color white while leaving a 1 or 2 pixel border
     along the edges of each of the resulting regions.
    You may assume that n is greater than or equal to 0.*/
    public static void divideCanvas(Color[][] pixels, int n) {
        divideHelper(pixels, 1, pixels[0].length - 1, 1, pixels.length - 1, n);
    }
}
