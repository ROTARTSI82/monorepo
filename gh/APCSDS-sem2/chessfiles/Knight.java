import java.awt.*;
import java.util.ArrayList;

public class Knight extends Piece
{
    public Knight(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_knight.gif", 3);
    }

    @Override
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<>();
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
