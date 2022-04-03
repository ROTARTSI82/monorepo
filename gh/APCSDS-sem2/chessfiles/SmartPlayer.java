import java.awt.*;
import java.util.ArrayList;

public class SmartPlayer extends Player
{

    public SmartPlayer(Board board, String name, Color color)
    {
        super(board, name, color);
    }

    public int score(Color col)
    {
        int acc = 0;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
            {
                Piece p = getBoard().get(new Location(r, c));
                if (p != null)
                    acc += (p.getColor().equals(col) ? 1 : -1) * p.getValue();
            }
        return acc;
    }

    @Override
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());

        int max = Integer.MIN_VALUE;
        Move sel = null;

        for (Move m : moves)
        {
            getBoard().executeMove(m);
            int score = score(getColor());
            if (score > max)
            {
                sel = m;
                max = score;
            }
            getBoard().undoMove(m);
        }
        return sel;
    }
}
