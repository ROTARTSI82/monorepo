/**
 * @author Grant Yang
 * @version 2025.10.10
 * CSE 123
 * C1: Abstract Strategy Game
 * TA: Benoit Le
 */

import java.util.Scanner;

/**
 * An implementation of the Connect Four game as an AbstractStrategyGame.
 * The game is played on a 7-column by 6-row grid, and players may additionally
 * remove the bottom piece of a column if that piece belongs to them.
 */
public class ConnectFour extends AbstractStrategyGame {

    private final static int WIDTH = 7;
    private final static int HEIGHT = 6;

    private final static long COLUMN = 0b000000100000010000001000000100000010000001L;

    private final long[] players;
    private int turn;

    /**
     * Constructs a new Connect Four game.
     * The game starts with player 1's turn.
     */
    public ConnectFour() {
        players = new long[]{0, 0};
        turn = 1;
    }

    /**
     * Retrieves instructions for playing Connect Four.
     * @return A human-readable string describing how to play the game.
     */
    @Override
    public String instructions() {
        return "connect 4 instructions here";
    }

    /**
     * Prints the current game state to a String as a grid of characters.
     * @return A string representing the grid, with a space before each character.
     *         Empty squares are represented by '.', player 1's pieces by '1',
     *         and player 2's pieces by '2'. Each row is terminated by a newline.
     */
    @Override
    public String toString() {
        String ret = ""; // why no StringBuilder? :(
        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                ret += ' ';
                long sq = 1L << (r * WIDTH + c);
                if ((players[0] & sq) != 0)
                    ret += '1';
                else if ((players[1] & sq) != 0)
                    ret += '2';
                else
                    ret += '.';
            }
            ret += '\n';
        }

        return ret;
    }

    /**
     *
     * @return
     */
    @Override
    public int getWinner() {
        return turn <= 0 ? -turn : -1;
    }

    /**
     *
     * @return
     */
    @Override
    public int getNextPlayer() {
        return turn <= 0 ? -1 : turn;
    }

    /**
     *
     * @param input
     * @throws IllegalArgumentException
     */
    private void checkValidMove(String input) {
        if ((input.length() != 1 || input.charAt(0) < '1' || input.charAt(0) > '0' + WIDTH)
                && (input.length() != 3 || (input.charAt(0) != 'R' && input.charAt(0) != 'A')
                    || input.charAt(2) < '1' || input.charAt(2) > '0' + WIDTH || input.charAt(1) != ' '))
            throw new IllegalArgumentException(input + " is not a valid move");
    }

    /**
     *
     * @param input
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public String getMove(Scanner input) {
        if (input == null)
            throw new IllegalArgumentException("getMove(scanner = null) called");
        String move = input.nextLine();
        checkValidMove(move);
        return move;
    }

    /**
     *
     * @param input
     * @throws IllegalArgumentException
     */
    @Override
    public void makeMove(String input) {
        if (input == null)
            throw new IllegalArgumentException("null is not a move");
        if (turn <= 0)
            throw new IllegalArgumentException("makeMove() called on finished game");

        checkValidMove(input);
        int col = input.charAt(input.length() == 3 ? 2 : 0) - '1';

        long colMask = COLUMN << col;
        long occ = players[0] | players[1];
        if (input.length() == 1 || input.charAt(0) == 'A') {
            if ((occ & (1L << col)) != 0L)
                throw new IllegalArgumentException(input + " - column already full!");
            long emptyBoard = ~0L << (WIDTH * HEIGHT);
            int row = -1 + Long.numberOfTrailingZeros(emptyBoard | (occ & colMask)) / WIDTH;
            players[turn - 1] |= 1L << (row * WIDTH + col);
        } else {
            int bottomSq = (HEIGHT - 1) * WIDTH + col;
            if ((players[turn - 1] & (1L << bottomSq)) == 0L)
                throw new IllegalArgumentException(input + " - bottom piece is not yours!");
            for (int i = 0; i < 2; i++)
                players[i] = (players[i] & ~colMask)
                        | ((players[i] & colMask) << WIDTH);
        }

        sweep(0b1111);
        sweep(0b0000001_0000001_0000001_0000001);
        sweep(0b0001000_0000100_0000010_0000001);
        sweep(0b0000001_0000010_0000100_0001000);

        if (turn > 0)
            turn ^= 0b11;
    }

    /**
     *
     * @param mask
     */
    private void sweep(long mask) {
        boolean wide = (0b1111 & mask) != 0;
        boolean tall = (COLUMN & mask) != 0;

        // I will just do this the lazy way lol
        for (int r = 0; r < HEIGHT - (tall ? 3 : 0); r++) {
            for (int c = 0; c < WIDTH - (wide ? 3 : 0); c++) {
                for (int i = 0; i < 2; i++) {
                    long smask = mask << (r * WIDTH + c);
                    if ((smask & players[i]) == smask) {
                        if (turn > 0)
                            turn = -(i + 1); // one player won
                        else if (turn != -(i + 1))
                            turn = 0; // tie!
                    }
                }
            }
        }
    }

}
