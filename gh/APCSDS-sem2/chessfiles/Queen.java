
import java.awt.*;
import java.util.ArrayList;

public class Queen extends Piece
{
    Queen(Color c)
    {
        super(c, (c.equals(Color.WHITE) ? "white" : "black") + "_queen.gif", 9);
    }

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

