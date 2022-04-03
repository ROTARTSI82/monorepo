import java.awt.*;
import java.io.IOException;

public class Game
{
    private static boolean turn;

    public static void main(String[] args) throws IOException
    {
        Board board = new Board();
        board.loadFen("rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR");

        System.out.println("FEN: " + board.getFen());

//        Player black = new RandomPlayer(board, "Black", Color.BLACK);

        BoardDisplay display = new BoardDisplay(board);

        Player white = new HumanPlayer(board, "You", Color.WHITE, display);
        Player black = new SmarterPlayer(board, "bass'd fish", Color.BLACK);

        play(board, display, white, black);
    }

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

    public static void play(Board board, BoardDisplay display, Player white, Player black)
    {
        for (turn = true; nextTurn(board, display, turn ? white : black); turn ^= true)
        {
            // do nothing
        }
    }
}
