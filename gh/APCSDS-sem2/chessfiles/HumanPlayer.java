import java.awt.*;
import java.util.ArrayList;

/**
 * A player that allows the user to select a move using the GUI
 * @author Grant Yang
 * @version 2022.04.03
 */
public class HumanPlayer extends Player
{
    private final BoardDisplay display;

    /**
     * Makes a new HumanPlayer
     * @param board Board to play chess with
     * @param name Name of the player
     * @param color Side, either Color.WHITE or Color.BLACK
     * @param disp Board display to select moves with
     */
    public HumanPlayer(Board board, String name, Color color, BoardDisplay disp)
    {
        super(board, name, color);
        display = disp;
    }

    /**
     * Asks the player for a move and returns it.
     * The move returned will be pseudo-legal, as we will
     * re-prompt on illegal moves
     * @return A move selected by the user
     */
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
