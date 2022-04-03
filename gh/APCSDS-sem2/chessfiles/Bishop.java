
import java.awt.*;
import java.util.ArrayList;

public class Bishop extends Piece
{
    public static final int[] SWEEPS = {Location.NORTHEAST, Location.NORTHWEST, Location.SOUTHEAST, Location.SOUTHWEST};

    Bishop(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_bishop.gif", 3);
    }

    @Override
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();
        for (int d : SWEEPS)
            sweep(ret, d);
        return ret;
    }
}

