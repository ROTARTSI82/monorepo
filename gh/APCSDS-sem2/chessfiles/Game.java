import java.awt.*;
import java.io.IOException;

/**
 * Main entry point for Chess Lab, launching a GUI
 * to play chess as white against SmarterPlayer.
 * @author Grant Yang
 * @version 2022.04.03
 */
public class Game
{
    private static boolean turn;

    /**
     * Entry point. Opens a GUI for playing chess against
     * SmarterPlayer
     * @param args Command line arguments
     * @throws IOException Thrown when communication with an UCI chess engine fails.
     */
    public static void main(String[] args) throws IOException
    {
        Board board = new Board();
        board.loadFen("rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR");

        System.out.println("FEN: " + board.getFen());

        BoardDisplay display = new BoardDisplay(board);

        Player white = new HumanPlayer(board, "You", Color.WHITE, display);
        Player black = new SmartestPlayer(board, "bass'd fish", Color.BLACK);

        play(board, display, white, black);
    }

    /**
     * Play one turn of chess
     * @param board Board to play chess on
     * @param display Display to display the game on
     * @param player Which player is to play a move
     * @return True if the game should continue, false if game is over
     */
    private static boolean nextTurn(Board board,
                                 BoardDisplay display, Player player)
    {
        board.setIsWhiteTurn(turn);

        display.setTitle(player.getName());
        Move m = player.nextMove();

        board.executeMove(m);
        display.clearColors();
        display.setColor(m.getSource(), Color.YELLOW);
        display.setColor(m.getDestination(), Color.BLUE);

        display.showBoard();

        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            // nothing
        }

        return true;
    }

    /**
     * Plays a game of chess until the end
     * @param board Board to play on
     * @param display Display to display the game on
     * @param white White player
     * @param black Black player
     */
    public static void play(Board board, BoardDisplay display, Player white, Player black)
    {
        for (turn = true; nextTurn(board, display, turn ? white : black); turn ^= true)
        {
            // do nothing
        }
    }
}
