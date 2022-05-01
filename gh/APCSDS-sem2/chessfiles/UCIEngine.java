import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Player that selects moves with an arbitrary UCI chess engine
 * @author Grant Yang
 * @version 2022.04.03
 */
public class UCIEngine extends Player
{
    private final Process proc;

    /**
     * Constructs a new UCIEngine
     * @param board Board to play chess on
     * @param name The command for the UCI engine (e.g. "stockfish" or "lc0").
     *             Also used as the name of the player
     * @param color Side, either Color.BLACK or Color.WHITE
     * @throws IOException If spawning the engine process failed
     */
    public UCIEngine(Board board, String name, Color color) throws IOException
    {
        super(board, name, color);
        proc = new ProcessBuilder(name).start();
    }

    /**
     * Calculates the best move with the specified UCI engine,
     * with `movetime 4000` (4 seconds)
     * @return The move selected by the engine
     *         NOTE: No legality checks are performed, thus
     *         castling, en-passants, and promotions WILL NOT
     *         function as expected.
     */
    @Override
    public Move nextMove()
    {
        PrintWriter writer = new PrintWriter(proc.getOutputStream());

        String fen = getBoard().getFen();
        System.out.println(fen);

        writer.println("position fen " + fen);
        writer.println("go movetime 4000");
//        writer.println("d");
        writer.flush();

        Scanner scan = new Scanner(proc.getInputStream());

        String s = "";
        while (s.indexOf("bestmove") != 0 && scan.hasNext())
        {
            try
            {
                s = scan.nextLine();
                System.out.println(s);
            }
            catch (Exception e)
            {
                // ignore
            }
        }
        System.out.println(s);

        s = s.substring(9, 13);
        return new Move(getBoard().get(new Location(7 - (s.charAt(1) - '1'), s.charAt(0) - 'a')),
                new Location(7 - (s.charAt(3) - '1'), s.charAt(2) - 'a'));
    }
}
