import java.awt.*;
import java.util.ArrayList;

/**
 * A searcher with a depth of 0, searching only 1 ply deep.
 * @author Grant Yang
 * @version 2022.04.03
 */
public class SmartPlayer extends Player
{

    /**
     * Constructs a new SmartPlayer
     * @param board Board to play chess on
     * @param name Name of the player
     * @param color Side of the player, either Color.WHITE or Color.BLACK
     */
    public SmartPlayer(Board board, String name, Color color)
    {
        super(board, name, color);
    }

    /**
     * Calculates the score of a position for the specified side in pawns
     * @param col The side to calculate score for, either Color.BLACK or Color.WHITE
     * @return The score of the position in pawns
     */
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

    /**
     * Calculates the next move by selecting the move
     * that will result in the max score after 1 ply
     * @return The selected pseudo-legal move
     */
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
