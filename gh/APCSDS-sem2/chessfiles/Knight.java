import java.awt.*;
import java.util.ArrayList;

/**
 * Knight piece in chess
 * @author Grant Yang
 * @version 2022.04.03
 */
public class Knight extends Piece
{
    /**
     * Makes a new knight
     * @param c Color of the knight, either Color.WHITE or Color.BLACK
     */
    public Knight(Color c)
    {
        super(c, "_knight.gif", 3);
    }

    /**
     * Gets a list of valid destinations to move to
     * @return A list of locations
     */
    @Override
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();
        Location o = getLocation();
        int r = o.getRow() - 2;
        int c = o.getCol() - 2;

        for (long pack = 123973744756257L; pack != 0; pack >>= 6)
        {
            Location l = new Location((int) (pack & 7) + r, (int) ((pack >> 3) & 7) + c);
            if (isValidDestination(l))
                ret.add(l);
        }

        return ret;
    }
}
