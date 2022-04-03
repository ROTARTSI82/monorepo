import java.awt.*;
import java.util.ArrayList;

public class King extends Piece
{
    public King(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_king.gif", 1000);
    }

    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<>();
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
