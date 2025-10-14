/**
 * @author Grant Yang
 * @version 2025.10.11
 * CSE 123
 * C1: Abstract Strategy Game
 * TA: Benoit Le
 */

import java.util.*;

/**
 * An implementation of Connect Four as an AbstractStrategyGame.
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
     * @return A human-readable description of how to play the game.
     */
    @Override
    public String instructions() {
        return """
        Welcome to Connect Four!
        
        Players take turns dropping their pieces into one of the 7 columns of the board.
        The first player to get four of their pieces in a row, column, or diagonal wins.
        
        To make a move, simply enter a column number (1-7). Alternatively, you can also
        write this as 'A {column #}'.
        
        Instead of dropping a piece on your turn, you can remove one of your own pieces
        from the bottom of a column by entering 'R {column #}', shifting all pieces above it down.
        
        If both players get a connect four on the same turn, the game is a draw,
        regardless of the number of connect fours each player got.
        Note that even if the board fills up, there will always be a valid removal move
        available, so the game cannot end in a draw due to a full board.
        """;
    }

    /**
     * Prints the current game state to a String as a grid of characters.
     * @return A visual representation of the grid, with a space before each character.
     *         Empty squares are represented by '.', player 1's pieces by '1',
     *         and player 2's pieces by '2'. Rows are separated by newlines.
     */
    @Override
    public String toString() {
        String ret = "["; // why no StringBuilder? :(
        for (int c = 1; c < WIDTH + 1; c++)
            ret += String.valueOf(c) + ' ';

        ret += "]\n";
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
     * Returns the winner of the game. A player may win by making a connect four
     * or if the other player has no pieces left to remove when the board is full.
     * A draw occurs if both players get a connect four on the same turn,
     * regardless of how many connect fours each player has.
     *
     * @return 1 if player 1 has won, 2 if player 2 has won, 0 if the game is a tie,
     *         and -1 if the game is not over.
     */
    @Override
    public int getWinner() {
        return turn <= 0 ? -turn : -1;
    }

    /**
     * Returns the player whose turn it is.
     * This value will flip between 1 and 2 with each call to makeMove() while the game is ongoing.
     *
     * @return 1 or 2 if the game is in progress, or -1 if the game is over.
     */
    @Override
    public int getNextPlayer() {
        return turn <= 0 ? -1 : turn;
    }

    /**
     * Helper for checking if the given player input is a syntactically valid move.
     * A valid move is either a single digit from 1-7 (inclusive) to add a piece,
     * or "R #" or "A #" where # is a digit from 1-7 to remove or add a piece.
     *
     * @param input The player's move as a String.
     * @throws IllegalArgumentException if the input is not a valid move.
     */
    private void checkValidMove(String input) {
        if ((input.length() != 1 || input.charAt(0) < '1' || input.charAt(0) > '0' + WIDTH) &&
                (input.length() != 3 || (input.charAt(0) != 'R' && input.charAt(0) != 'A') ||
                input.charAt(2) < '1' || input.charAt(2) > '0' + WIDTH || input.charAt(1) != ' '))
            throw new IllegalArgumentException(input + " is not a valid move");
    }

    /**
     * Reads the next line of input from the user and returns it as the desired move.
     *
     * @param input The scanner to read the move from. The scanner should have a next line to read.
     * @return The player's desired move as a String.
     * @throws IllegalArgumentException if the given Scanner is null, or if the line
     *                                  read from the scanner is not a valid move.
     */
    @Override
    public String getMove(Scanner input) {
        if (input == null)
            throw new IllegalArgumentException("getMove(scanner = null) called");
        // should this just return input.nextLine() without checking validity?
        String move = input.nextLine();
        checkValidMove(move);
        return move;
    }

    /**
     * Makes a move on the board. This can be either adding a piece to a column
     * or removing the player's own piece from the bottom of a column.
     * After this call, the new game state can be queried by getWinner() and getNextPlayer().
     * If the move is invalid, the state of the ConnectFour game will not have changed.
     *
     * @param input The move to be made, as a String. Can be a single digit from 1-7
     *              or 'A [1-7]' to place a piece in a 1-indexed column,
     *              or 'R [1-7]' to remove a piece as described.
     * @throws IllegalArgumentException if the move is null, the game is over, the move is
     *                                  syntactically invalid, the target column is full,
     *                                  or if a player tries to remove a piece that is not
     *                                  theirs from the bottom of a column.
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

        // never possible to draw here as a full board is guaranteed to have a valid removal move.
        // Full board: ((players[0] | players[1]) & ~EMPTY_BOARD) == ~EMPTY_BOARD
        if (turn > 0)
            turn ^= 0b11; // game continues with other player's turn
    }

    /**
     * Scans the board for a four-in-a-row pattern, defined by the given mask.
     * This method iterates through all possible positions on the board where the
     * pattern could start. If a win is detected, it updates the game's turn state
     * to reflect the winner. If multiple players win in the same turn, it results
     * in a tie.
     *
     * @param mask A bitmask on the 7x6 board where set bits represent the shape of the winning
     *             pattern to check for (e.g., horizontal, vertical, or diagonal).
     *             The pattern should be 1x4, 4x1, or 4x4, placed in the upper left of the board.
     *             The least significant bit corresponds to the upper left corner,
     *             and we enumerate the squares left-to-right, top-to-bottom.
     */
    private void sweep(long mask) {
        boolean wide = (~COLUMN & mask) != 0;
        boolean tall = (~0b1111 & mask) != 0;

        for (int r = 0; r < HEIGHT - (tall ? 3 : 0); r++) {
            for (int c = 0; c < WIDTH - (wide ? 3 : 0); c++) {
                for (int i = 0; i < 2; i++) {
                    long smask = mask << (r * WIDTH + c);
                    if ((smask & players[i]) == smask) {
                        if (turn > 0)
                            turn = -(i + 1); // one player won
                        else if (turn != -(i + 1))
                            turn = 0; // tie! both players got at least one connect 4
                    }
                }
            }
        }
    }

}
