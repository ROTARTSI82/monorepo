import java.util.ArrayList;

/**
 * A class to represent a bounded, rectangular 2d grid
 * of objects
 * @param <E> The type of the object contained in the grid.
 * @author Grant Yang
 * @version 2022.02.26
 */
public class MyBoundedGrid<E>
{
    private final Object[][] grid;
    private final int r, c;

    /**
     * Constructs a new grid with the specified dimensions
     * @param r Number of rows in the grid
     * @param c Number of columns in the grid
     */
    public MyBoundedGrid(int r, int c)
    {
        this.r = r;
        this.c = c;

        grid = new Object[r][c];

    }

    /**
     * Getter method for the number of rows in the grid.
     * @return The number of rows
     */
    public int getNumRows()
    {
        return r;
    }

    /**
     * Getter method for the number of columns in the grid.
     * @return The number of columns
     */
    public int getNumCols()
    {
        return c;
    }

    /**
     * Checks that a Location is valid and in-bounds
     * to the grid in O(1)
     * @param loc Location to verify
     * @return true if the location is on the grid, false otherwise
     */
    public boolean isValid(Location loc)
    {
        return loc != null && loc.getRow() >= 0 && loc.getRow() < r
                           && loc.getCol() >= 0 && loc.getCol() < c;
    }

    /**
     * Puts an item into the grid at the specified location in O(1)
     * @precondition The location specified is valid
     * @throws RuntimeException if preconditions are not met
     * @postcondition The grid would contain obj at the specified location
     * @param loc Location to put the object at
     * @param obj The value to put at the location
     * @return The previous value at that location, null if there was none.
     */
    public E put(Location loc, E obj)
    {
        if (!isValid(loc) /* || obj == null */ )
            throw new RuntimeException("put() with null object or invalid location");
        E ret = (E) grid[loc.getRow()][loc.getCol()];
        grid[loc.getRow()][loc.getCol()] = obj;
        return ret;
    }

    /**
     * Removes an object from the grid at a specified location in O(1)
     * @precondition The location specified is valid
     * @throws RuntimeException if preconditions are not met
     * @param loc Location to clear
     * @return The value of the previous value at loc, null if there was none.
     */
    public E remove(Location loc)
    {
        if (!isValid(loc))
            throw new RuntimeException("remove() on invalid location");
        E ret = (E) grid[loc.getRow()][loc.getCol()];
        grid[loc.getRow()][loc.getCol()] = null;
        return ret;
    }

    /**
     * Retrieves a value at a specific location in the grid in O(1)
     * @precondition loc is a valid Location
     * @throws RuntimeException if preconditions are not met
     * @param loc Location to query the value of
     * @return The value stored at the location, null if there is none.
     */
    public E get(Location loc)
    {
        if (!isValid(loc))
            throw new RuntimeException("get() on invalid location");
        return (E) grid[loc.getRow()][loc.getCol()];
    }

    /**
     * Builds a list of non-null locations in O(n) with n
     * being the number of unique locations within the grid.
     * @return A list of occupied Location objects in this grid
     */
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
