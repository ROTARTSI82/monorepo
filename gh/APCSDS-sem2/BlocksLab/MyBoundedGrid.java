import java.util.ArrayList;

public class MyBoundedGrid<E>
{
    private Object[][] grid;
    private final int r, c;

    public MyBoundedGrid(int r, int c)
    {
        this.r = r;
        this.c = c;

        grid = new Object[r][c];

    }

    public int getNumRows()
    {
        return r;
    }

    public int getNumCols()
    {
        return c;
    }

    public boolean isValid(Location loc)
    {
        return loc.getRow() >= 0 && loc.getRow() < r && loc.getCol() >= 0 && loc.getCol() < c;
    }

    public E put(Location loc, E obj)
    {
        if (!isValid(loc) || obj == null)
            throw new RuntimeException("put() with null object or invalid location");
        E ret = (E) grid[loc.getRow()][loc.getCol()];
        grid[loc.getRow()][loc.getCol()] = obj;
        return ret;
    }

    public E remove(Location loc)
    {
        if (!isValid(loc))
            throw new RuntimeException("remove() on invalid location");
        E ret = (E) grid[loc.getRow()][loc.getCol()];
        grid[loc.getRow()][loc.getCol()] = null;
        return ret;
    }

    public E get(Location loc)
    {
        if (!isValid(loc))
            throw new RuntimeException("get() on invalid location");
        return (E) grid[loc.getRow()][loc.getCol()];
    }

    public ArrayList<Location> getOccupiedLocations()
    {
        ArrayList<Location> ret = new ArrayList<Location>();
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                if (grid[i][j] != null)
                    ret.add(new Location(i, j));
        return ret;
    }

}
