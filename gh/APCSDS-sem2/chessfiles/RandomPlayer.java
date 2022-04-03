import java.awt.*;
import java.util.ArrayList;

/**
 * A player for chess that plays a random pseudo-legal move
 * @author Grant Yang
 * @version 2022.04.03
 */
public class RandomPlayer extends Player
{
    /**
     * Makes a new random player
     * @param b Board to play chess with
     * @param n Name of the player
     * @param c Side, either Color.WHITE or Color.BLACK
     */
    public RandomPlayer(Board b, String n, Color c)
    {
        super(b, n, c);
    }

    /**
     * Gets a random pseudo-legal move to play
     * @return A random Move
     */
    @Override
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());
        return moves.get((int) (Math.random() * moves.size()));
    }
}
