import java.awt.*;
import java.util.ArrayList;

/**
 * Queen from the hit 2020 video game Chess
 * @author Grant Yang
 * @version 2022.04.03
 */
public class Queen extends Piece
{
    /**
     * Constructs a new Queen
     * @param c Side of the queen, either Color.BLACK or Color.WHITE
     */
    Queen(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_queen.gif", 9);
    }

    /**
     * Retrieves a list of the possible destinations for the queen
     * @return An array of Locations this queen can move to
     */
    @Override
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();
        for (int d : Rook.SWEEPS)
            sweep(ret, d);

        for (int d : Bishop.SWEEPS)
            sweep(ret, d);

        return ret;
    }
}

