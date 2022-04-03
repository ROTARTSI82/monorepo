import java.awt.*;
import java.util.ArrayList;

public class HumanPlayer extends Player
{
    private final BoardDisplay display;

    public HumanPlayer(Board board, String name, Color color, BoardDisplay disp)
    {
        super(board, name, color);
        display = disp;
    }

    @Override
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());
        Move m = display.selectMove();
        for (Move leg : moves)
            if (leg.equals(m))
                return m;
        return nextMove();
    }
}
