import java.awt.*;
import java.util.ArrayList;

/**
 * An alpha-beta searcher with a depth of 1 (searching 2 plies ahead)
 * @version 2022.04.03
 * @author Grant Yang
 */
public class SmarterPlayer extends SmartPlayer
{
    /**
     * Constructs a new SmartPlayer
     * @param board Board to play chess on
     * @param name Name of the player
     * @param color Side of the player, either Color.WHITE or Color.BLACK
     */
    public SmarterPlayer(Board board, String name, Color color)
    {
        super(board, name, color, 1);
    }

}
