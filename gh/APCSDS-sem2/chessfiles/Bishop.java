import java.awt.*;
import java.util.ArrayList;

/**
 * Bishop in chess, moving along diagonal rays
 * @author Grant Yang
 * @version 2022.04.03
 */
public class Bishop extends Piece
{
    /**
     * List of directions to weep in for bishop moves
     * Is also used in Queen class.
     */
    public static final int[] SWEEPS = {Location.NORTHEAST, Location.NORTHWEST, Location.SOUTHEAST,
            Location.SOUTHWEST};

    /**
     * Make a new black or white bishop
     * @param c Either Color.BLACK or Color.WHITE
     */
    Bishop(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_bishop.gif", 3);
    }

    /**
     * Get a list of destinations for this piece
     * @return An array of Locations
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

