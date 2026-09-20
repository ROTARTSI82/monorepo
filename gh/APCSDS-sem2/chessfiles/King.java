import java.awt.*;
import java.util.ArrayList;

/**
 * A king, the most important piece in a game of chess
 * @author Grant Yang
 * @version 2022.04.03
 */
public class King extends Piece
{
    /**
     * Constructs a new king
     * @param c Color of king, either Color.WHITE or Color.BLACK
     */
    public King(Color c)
    {
        super(c, "_king.gif", 1000);
    }

    /**
     * Gets a list of possible destinations for this king
     * @return An array of all locations this king can move to
     */
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();
        Location o = getLocation();
        int r = o.getRow() - 1;
        int c = o.getCol() - 1;

        for (long pack = 2844148240L; pack != 0; pack >>= 4)
        {
            Location l = new Location((int) (pack & 3) + r, (int) ((pack >> 2) & 3) + c);
            if (isValidDestination(l))
                ret.add(l);
        }

        return ret;
    }
}
