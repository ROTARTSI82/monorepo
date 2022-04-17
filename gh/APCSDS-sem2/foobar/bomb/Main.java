
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("1 = " + Solution.solution("2", "1"));
        System.out.println("4 = " + Solution.solution("4", "7"));

        Scanner scan = new Scanner(System.in);
        for (;;) {
            System.out.println(" := " + Solution.solution(scan.nextBigInteger().toString(), scan.nextBigInteger().toString()));
        }
    }
}
