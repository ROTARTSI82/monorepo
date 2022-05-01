import java.awt.*;
import java.util.ArrayList;

/**
 * A pawn from the hit game Chess (En Passant DLC must be purchased separately)
 * @version 2022.04.03
 * @author Grant Yang
 */
public class Pawn extends Piece
{
    /**
     * Constructions a new pawn
     * @param c Color of the pawn, either Color.WHITE or Color.BLACK
     */
    public Pawn(Color c)
    {
        super(c, "_pawn.gif", 1);
    }

    /**
     * Gets a list of valid destinations for this pawn
     * @return A list of locations
     */
    @Override
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();

        int off;
        if (getColor().equals(Color.WHITE))
            off = -1;
        else off = 1;
        Location targ = new Location(getLocation().getRow() + off, getLocation().getCol());

        if (getBoard().isValid(targ) && getBoard().get(targ) == null)
        {
            ret.add(targ);
            if (getLocation().getRow() == 1 && getColor().equals(Color.BLACK))
            {
                Location targ2 = targ.getAdjacentLocation(Location.SOUTH);
                if (getBoard().isValid(targ2) && getBoard().get(targ2) == null) ret.add(targ2);
            }
            else if (getLocation().getRow() == 6 && getColor().equals(Color.WHITE))
            {
                Location targ2 = targ.getAdjacentLocation(Location.NORTH);
                if (getBoard().isValid(targ2) && getBoard().get(targ2) == null) ret.add(targ2);
            }
        }

        Location tmp = targ.getAdjacentLocation(Location.WEST);
        if (isValidDestination(tmp) && getBoard().get(tmp) != null)
            ret.add(tmp);
        tmp = targ.getAdjacentLocation(Location.EAST);
        if (isValidDestination(tmp) && getBoard().get(tmp) != null)
            ret.add(tmp);

        return ret;
    }
}
