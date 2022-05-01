import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Main entry point for Chess Lab, launching a GUI
 * to play chess as white against SmarterPlayer.
 * @author Grant Yang
 * @version 2022.04.03
 */
public class Game
{
    private static boolean turn;
    private static boolean gameOver = false;

    /**
     * Entry point. Opens a GUI for playing chess against
     * SmarterPlayer
     * @param args Command line arguments
     * @throws IOException Thrown when communication with an UCI chess engine fails.
     */
    public static void main(String[] args) throws IOException
    {
        int sel = -1;
        Scanner scan = new Scanner(System.in);
        while (sel < 0 || sel > 5)
        {
            System.out.println("""
                    Select opponent:
                    \t0) Stockfish
                    \t1) C++ Engine (Scacus)
                    \t2) Human Player
                    \t3) Random Player
                    \t4) Smart Player (depth=0)
                    \t5) Smarter Player (depth=1)
                    \t6) Smartest Player (depth=5)""");
            System.out.print("Enter you selection [0-6]: ");
            try
            {
                sel = scan.nextInt();
                System.out.println("Ok!");
            }
            catch (Exception e)
            {
                System.out.println("Invalid input! Try again");
            }
        }

        Board board = new Board();
//        board.loadFen("rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR");
        board.loadFen("r3k2r/pppppppp/8/8/1nbq1bn1/8/PPPPPPPP/RNBQKBNR");

        System.out.println("FEN: " + board.getFen());

        BoardDisplay display = new BoardDisplay(board);

        Player white = new HumanPlayer(board, "You", Color.WHITE, display);
        Player black = new Player[]{
                new UCIEngine(board, "stockfish", Color.BLACK),
                new UCIEngine(board, "./Scacus/build/Scacus", Color.BLACK),
                new HumanPlayer(board, "Black", Color.BLACK, display),
                new RandomPlayer(board, "Random - Black", Color.BLACK),
                new SmartPlayer(board, "SmartPlayer (d=0) - Black", Color.BLACK),
                new SmarterPlayer(board, "SmarterPlayer (d=1) - Black", Color.BLACK),
                new SmartestPlayer(board, "SmartestPlayer (d=5) - Black", Color.BLACK),
        }[sel];

        display.showBoard();
        play(board, display, white, black);
        display.showBoard();
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


        boolean inCheck = false;
        ArrayList<Move> moves = board.allMoves(player.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE);
        for (Move i : board.allMoves(player.getColor()))
        {
            if (board.get(i.getDestination()) instanceof King)
            {
                System.out.println("CHECK!");
                inCheck = true;
                display.setColor(i.getDestination(), Color.RED);
                break;
            }
        }

        HumanPlayer.rmIllegalMoves(moves.iterator(), board, player.getColor());

        gameOver |= moves.isEmpty();
        if (gameOver)
        {
            System.out.println(inCheck ? "CHECKMATE" : "STALEMATE!");
            if (inCheck)
                display.setTitle("Game Over - " +
                        (player.getColor() == Color.WHITE ? "White" : "Black") + " wins!");
            else
                display.setTitle("Game Over - Draw");
        }

        display.showBoard();

        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            // nothing
        }

        return !gameOver;
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
