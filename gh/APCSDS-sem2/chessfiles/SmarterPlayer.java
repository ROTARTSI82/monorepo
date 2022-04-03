import java.awt.*;
import java.util.ArrayList;

/**
 * An alpha-beta searcher with a depth of 1 (searching 2 plies ahead)
 * @version 2022.04.03
 * @author Grant Yang
 */
public class SmarterPlayer extends SmartPlayer
{
    private final int searchDepth;

    /**
     * Constructs a new player
     * @param board Board to play chess on
     * @param name Name of the player
     * @param color Side, either Color.BLACK or Color.WHITE
     */
    public SmarterPlayer(Board board, String name, Color color)
    {
        this(board, name, color, 1);
    }

    /**
     * Constructs a new player
     * @param b Board to play chess on
     * @param name Name of the player
     * @param c Side, either Color.BLACK or Color.WHITE
     * @param depth Depth of the search, will search 1+depth plies deep.
     */
    public SmarterPlayer(Board b, String name, Color c, int depth)
    {
        super(b, name, c);
        searchDepth = depth;
    }

    /**
     * Performs a negamax alpha-beta search
     * @param alpha Alpha value (i.e. our best-case)
     * @param beta Beta value (i.e. the best-case for our opponent,
     *             if our best-case alpha value exceeds the best-case beta value
     *             for our opponent, our opponent will never go down this branch of the
     *             game tree as they already have a better path. Thus, we can stop
     *             searching on this branch)
     * @param isWhite Perspective to evaluate the position from,
     *                true if white is to move, false if black.
     * @param depth How many more plies to search (0 for static evaluation)
     * @return Score of the current position as revealed by the search
     */
    private int search(int alpha, int beta, boolean isWhite, int depth)
    {
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

    /**
     * Calculates the best next move with a negamax alpha-beta search
     * @return Best pseudo-legal move
     */
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

            int score = -search(-beta, -alpha, nextTurnIsWhite, searchDepth);

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
