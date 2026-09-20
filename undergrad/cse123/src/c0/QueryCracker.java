/**
 * @author Grant Yang
 * @version 2025.09.25
 * CSE 123
 * C0: Debugging
 * TA: Benoit Le
 */

import java.io.*;

public class QueryCracker {
    public static final int START = 123;
    public static final int CODE_LENGTH = 5;
    
    public static void unlock(String code) throws Exception {
        if (code.length() != CODE_LENGTH) {
            throw new QueryCracker.Deny();
        }

        String curr = Integer.toHexString(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            curr = scramble(curr);
            if (curr.charAt(0) != code.charAt(i)) {
                throw new QueryCracker.Deny();
            }
        }
    }

    private static String scramble(String secret) {
        return Integer.toHexString(Integer.parseInt(secret, 16) * 0xF6 / 0b100110);
    }

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("Search results ready. Unlock with key...");
        
        System.setOut(new PrintStream(new OutputStream() { public void write(int b) {} }));
        unlock("2c52d");    // found the code! :)
        System.setOut(SYSTEM_OUT);
        
        System.out.println("Key found! Search results unlocked!");
        System.out.println();
    }

    public static final PrintStream SYSTEM_OUT = System.out;
    private static class Deny extends RuntimeException {
        public Deny() { super("ACCESS DENIED"); }
    }
}
