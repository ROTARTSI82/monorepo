import java.awt.*;
import java.util.ArrayList;

public class Rook extends Piece
{
    public static final int[] SWEEPS = {Location.NORTH, Location.SOUTH, Location.WEST, Location.EAST};

    Rook(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_rook.gif", 5);
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
