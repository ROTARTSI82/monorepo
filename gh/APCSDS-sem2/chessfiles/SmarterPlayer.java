import java.awt.*;
import java.util.ArrayList;

public class SmarterPlayer extends SmartPlayer
{
    public SmarterPlayer(Board board, String name, Color color)
    {
        super(board, name, color);
    }

    private int search(int alpha, int beta, boolean isWhite, int depth)
    {
//        System.out.println("a = " + alpha + ", b = " + beta);
        if (depth <= 0)
            return score(isWhite ? Color.WHITE : Color.BLACK);

        ArrayList<Move> moves = getBoard().allMoves(isWhite ? Color.WHITE : Color.BLACK);

        int val = Integer.MIN_VALUE + 8;

        for (Move m : moves)
        {
            getBoard().executeMove(m);

            val = Math.max(val, -search(-beta, -alpha, !isWhite, depth - 1));
            alpha = Math.max(val, alpha);

            getBoard().undoMove(m);

            if (alpha >= beta)
                return val;
        }

        return val;
    }

    @Override
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());

        Move sel = null;

        int alpha = Integer.MIN_VALUE + 8;
        int beta = Integer.MAX_VALUE - 8;

        boolean nextTurnIsWhite = !getColor().equals(Color.WHITE);

        for (Move m : moves)
        {
            getBoard().executeMove(m);

            int score = -search(-beta, -alpha, nextTurnIsWhite, 5);

            if (score > alpha)
            {
                sel = m;
                alpha = score;
            }

            getBoard().undoMove(m);
        }

        System.out.println("Eval: " + alpha);

        return sel;
    }
}
