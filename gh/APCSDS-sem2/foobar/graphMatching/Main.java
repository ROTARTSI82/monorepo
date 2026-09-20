package graphMatching;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException  {
        Random rand = new Random();

//        FileWriter out = new FileWriter("out.csv");
//        for (int i = 0; i < 256; i++)
//            for (int j = 0; j < 256; j++) {
//                Solution.cache.clear();
//                if (!Solution.cycles(i, j)) {
//                    System.out.println(i + "," + j + "\n");
//                    out.write(i + "," + j + "\n");
//                }
//            }
//
//        out.flush();

        long avg = 0;

        for (int it = 0; it < 4096; it++) {
            int[] test = new int[100];
            for (int i = 0; i < 100; i++) {
                test[i] = rand.nextInt(0, 1073741823);
                System.out.println(test[i]);
            }

            long now = System.nanoTime();
            System.out.println(Solution.solution(test));
//            System.out.println(it + " = " + Solution.solution(test) + " | acc = " + avg);
            long diff = System.nanoTime() - now;

            avg += diff;
        }

        System.out.println("Solve took: " + (avg / 4096.0 / 1e9) + " sec");

//        System.out.println("Should be 0 := " + Solution.solution(new int[]{1, 7, 3, 21, 13, 19}));
//
//        Solution.cache.clear();
//        System.out.println("Loops: " + Solution.cycles(24, 8));
//        System.out.println("Should be 2 := " + Solution.solution(new int[]{1, 1}));
    }
}
