import java.awt.*;
import java.util.ArrayList;

/**
 * A rook from popular 2002 indie game Chess (castling DLC not included)
 * @author Grant Yang
 * @version 2022.04.03
 */
public class Rook extends Piece
{
    /**
     * List of all directions to sweep in for rook moves.
     * Also used in Queen class
     */
    public static final int[] SWEEPS = {Location.NORTH, Location.SOUTH,
                                        Location.WEST, Location.EAST};

    /**
     * Constructs a new Rook
     * @param c Side, either Color.WHITE or Color.BLACK
     */
    Rook(Color c)
    {
        super(c,  "_rook.gif", 5);
    }

    /**
     * Gets a list of all possible destinations for this rook
     * @return An array of all locations this rook can move to
     */
    @Override
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();

        for (int d : SWEEPS)
            sweep(ret, d);

        return ret;
    }
}
