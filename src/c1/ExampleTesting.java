import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ExampleTesting {

    @Test
    @DisplayName("EXAMPLE TEST CASE - Small TicTacToe Example")
    public void firstCaseTest() {
        AbstractStrategyGame g = new TicTacToe1D();

        // You can add optional error messages that will be displayed if a test fails
        assertEquals(TicTacToe1D.PLAYER_1, g.getNextPlayer(), "Player 1 not next player after construction");
        assertEquals(TicTacToe1D.GAME_NOT_OVER, g.getWinner(), "Winner incorrectly declared after construction");
        assertFalse(g.isGameOver(), "Game over immediately after construction");

        // No need to create any Scanners to interact with getMove. We can just call makeMove for 
        // ease of testing!
        g.makeMove("1");

        assertEquals(TicTacToe1D.PLAYER_2, g.getNextPlayer(), "Player 2 not next player after a single move");
        assertEquals(TicTacToe1D.GAME_NOT_OVER, g.getWinner(), "Winner incorrectly declared after a single move");
        assertFalse(g.isGameOver(), "Game over immediately after construction");

        assertThrows(IllegalArgumentException.class, () -> {
            // 0 is an illegal move so our code should throw an IllegalArgumentException
            g.makeMove("0");
        }, "IllegalArgumentException not thrown for illegal move");
    }

    @Test
    @DisplayName("EXAMPLE TEST CASE - Large TicTacToe Example")
    public void secondCaseTest() {
        // You definitely don't have to get this fancy in your tests!
        AbstractStrategyGame g = new TicTacToe1D();

        // Going to play a whole game where 1 plays in first row, 2 plays in second row
        // No optional error messages - up to you if you want your code to be easier to debug!
        for (int i = 0; i < 5; i++) {
            int player = (i % 2) + 1;
            assertEquals(player, g.getNextPlayer());
            assertFalse(g.isGameOver());

            int cell = i / 2 + 1 + (player - 1) * 3;
            g.makeMove(String.valueOf(cell));
        }

        // At this point, 5 moves have been played, player 1 should have three in a row and
        // player 2 should have two
        assertTrue(g.isGameOver());
        assertEquals(TicTacToe1D.PLAYER_1, g.getWinner());
        assertEquals(TicTacToe1D.GAME_IS_OVER, g.getNextPlayer());
    }
}

