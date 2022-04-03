import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class UCIEngine extends Player
{
    private final Process proc;
    public UCIEngine(Board board, String name, Color color) throws IOException
    {
        super(board, name, color);
        proc = new ProcessBuilder(name).start();
    }

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
        while (s.indexOf("bestmove") != 0)
        {
            s = scan.nextLine();
        }
        System.out.println(s);

        s = s.substring(9, 13);
        return new Move(getBoard().get(new Location(7 - (s.charAt(1) - '1'), s.charAt(0) - 'a')),
                new Location(7 - (s.charAt(3) - '1'), s.charAt(2) - 'a'));
    }
}
