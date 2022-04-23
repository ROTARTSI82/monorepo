package nebula;

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
    }
}
