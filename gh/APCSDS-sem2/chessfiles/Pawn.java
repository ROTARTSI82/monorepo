import java.awt.*;
import java.util.ArrayList;

public class Pawn extends Piece
{
    public Pawn(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_pawn.gif", 1);
    }

    @Override
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();

        Location targ = new Location(getLocation().getRow() + (getColor().equals(Color.WHITE) ? -1 : 1), getLocation().getCol());
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
