import java.awt.*;
import java.util.ArrayList;

public class RandomPlayer extends Player
{
    public RandomPlayer(Board b, String n, Color c)
    {
        super(b, n, c);
    }

    @Override
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());
        return moves.get((int) (Math.random() * moves.size()));
    }
}
