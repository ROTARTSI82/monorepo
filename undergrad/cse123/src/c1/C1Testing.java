/**
 * @author Grant Yang
 * @version 2025.10.11
 * CSE 123
 * C1: Abstract Strategy Game
 * TA: Benoit Le
 */

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A suite of unit tests for the Connect Four game.
 */
public class C1Testing {

    private AbstractStrategyGame game;

    /**
     * Tests player 1 can win with a simple diagonal connect 4.
     */
    @Test
    @DisplayName("Simple P1 Win")
    public void testSimpleP1Win() {
        makeMoves(new int[]{2, 3, 3, 4, 5, 4, 4, 5, 6, 5}, 1);
        game.makeMove("5");
        assertWinner(1);
    }

    /**
     * Tests that player 2 can win with a simple vertical connect 4.
     */
    @Test
    @DisplayName("Simple P2 Win")
    public void testSimpleP2Win() {
        makeMoves(new int[]{1,2,1,2,1,2,3}, 1);
        game.makeMove("2");
        assertWinner(2);
    }

    /**
     * Tests that the game is drawn when two players get connect 4 at the same time.
     */
    @Test
    @DisplayName("Draw condition - Double Connect 4")
    public void testDrawDoubleConnect4() {
        // fills up the board with a funny pattern that has no winner
        makeMoves(new int[]{
                1,1,2,2,3,3,5,4,4,5,6,6,7,7,
                7,7,6,6,5,4,4,5,3,3,2,2,1,1,
                1,1,2,2,3,3,5,4,4,5,6,6,7,7
        }, 1);

        game.makeMove("R 2");
        assertWinner(0);
    }

    /**
     * Tests that trying to make illegal moves throws exceptions,
     * and that it does not corrupt the game state.
     */
    @Test
    @DisplayName("Illegal moves")
    public void testIllegalMoves() {
        makeMoves(new int[]{1,2,3,4,5,6,7}, 1);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("R 1"),
                "Should not be able to remove other player's piece");

        makeMoves(new int[]{1,2,3,4,5,6,7}, 2);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("R 2"),
                "Should not be able to remove other player's piece");

        makeMoves(new int[]{1,2,3,4,5,6,7}, 1);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("not a move"),
                "Should not be able to make nonsense move");

        makeMoves(new String[]{"R 2", "R 2", "4", "R 5", "R 2"}, 2);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("A 8"),
                "Should not be able to place in oob column");

        makeMoves(new int[]{5, 5, 5, 5}, 1);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("5"),
                "Should not be able to add to a full column");

        game.makeMove("6");
        assertWinner(1);
    }

    /**
     * Sets up a new game before each test.
     */
    @BeforeEach
    public void setup() {
        game = new ConnectFour();
        assertEquals(1, game.getNextPlayer(), "Player 1 should be first");
        assertFalse(game.isGameOver(), "Game should not be over at start");
        assertEquals(-1, game.getWinner(), "No winner at start");
    }

    /**
     * Makes a single move in the game and checks invariants.
     * @param move The move to make
     * @param turn The player whose turn it is before the move (1 or 2).
     */
    private void makeMove(String move, int turn) {
        game.makeMove(move);
        System.out.println(move + "\n" + game);

        turn ^= 0b11;
        assertEquals(turn, game.getNextPlayer(), "Expected turn to flip");
        assertFalse(game.isGameOver());
        assertEquals(-1, game.getWinner(), "No winner expected yet");
    }

    /**
     * Plays a sequence of moves, checking invariants along the way.
     * @param moves An array of column numbers (1-7) representing moves to be played in order.
     *              All moves must be legal in the order given.
     * @param turn The player whose turn it is before the start of the sequence (1 or 2).
     */
    private void makeMoves(int[] moves, int turn) {
        for (int move : moves) {
            makeMove(String.valueOf(move), turn);
            turn ^= 0b11;
        }
    }

    /**
     * Plays a sequence of moves, checking invariants along the way.
     * @param moves An array of move strings representing moves to be played in order.
     *              All moves must be legal in the order given.
     * @param turn The player whose turn it is before the start of the sequence (1 or 2).
     */
    private void makeMoves(String[] moves, int turn) {
        for (String move : moves) {
            makeMove(move, turn);
            turn ^= 0b11;
        }
    }

    /**
     * Checks that the game has ended, with the expected winner.
     * @param winner The winner we expect (1 or 2), or 0 for a draw.
     */
    private void assertWinner(int winner) {
        System.out.println(game);
        assertEquals(-1, game.getNextPlayer(), "Game should be over");
        assertTrue(game.isGameOver());
        assertEquals(winner, game.getWinner(), "Wrong winner for game");
    }
}