package nebula;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

        System.out.println("");

        // 245 its -> 209 its (14.7%)
        System.out.println("4=" + Solution.solution(new boolean[][]{{true, false, true},
                {false, true, false},
                {true, false, true}}));


        System.out.println("------------------------------");

        // 26888 its -> 9637 its (64.2%)
        System.out.println("254=" + Solution.solution(new boolean[][]{{true, false, true, false, false, true, true, true},
                {true, false, true, false, false, false, true, false},
                {true, true, true, false, false, false, true, false},
                {true, false, true, false, false, false, true, false},
                {true, false, true, false, false, true, true, true}}));

        System.out.println("------------------------------");

        // 183157 its -> 37603 its (79.47%)
        System.out.println("11567=" + Solution.solution(new boolean[][]{{true, true, false, true, false, true, false, true, true, false},
                {true, true, false, false, false, false, true, true, true, false},
                {true, true, false, false, false, false, false, false, false, true},
                {false, true, false, false, false, false, true, true, false, false}}));

//        Random rand = new Random();
//        for (int i = 0; i < 4096; i++) {
//            System.out.println("------------------------------");
//
//            int w = 3 + rand.nextInt(48);
//            int h = 3 + rand.nextInt(7);
//
//            boolean[][] inp = new boolean[h][w];
//            for (int r = 0; r < h; r++)
//                for (int c = 0; c < w; c++)
//                    inp[r][c] = rand.nextBoolean();
//
//            System.out.printf("w=%d, h=%d\n", w, h);
//            System.out.println(Solution.solution(inp));
//        }
    }
}
