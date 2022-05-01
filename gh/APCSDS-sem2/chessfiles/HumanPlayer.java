import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

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
        rmIllegalMoves(moves.iterator(), getBoard(),
                getColor() == Color.WHITE ? Color.BLACK : Color.WHITE);

        Move m = display.selectMove();
        for (Move leg : moves)
            if (leg.getDestination().equals(m.getDestination()) &&
                    leg.getPiece().getLocation().equals(m.getPiece().getLocation()))
                return leg;
        return nextMove();
    }

    /**
     * Filters out all illegal moves from a container
     * @param it Iterator into the container from which
     *           illegal moves should be removed
     * @param board The current state of the board
     * @param color The opposing color to the side currently making moves
     */
    public static void rmIllegalMoves(Iterator<Move> it, Board board, Color color)
    {
        while (it.hasNext())
        {
            Move test = it.next();
            board.executeMove(test);
            for (Move i : board.allMoves(color))
                if (board.get(i.getDestination()) instanceof King)
                {
                    it.remove();
                    break;
                }
            board.undoMove(test);
        }
    }
}
