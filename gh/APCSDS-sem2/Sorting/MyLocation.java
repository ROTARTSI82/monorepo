/**
 * Location class to represent a row and column ordered pair.
 * @author Grant Yang
 * @version 2022.02.26
 */
public class MyLocation implements Comparable
{
    private int row;
    private int col;

    /**
     * Constructor: MyLocation()
     * Usage: MyLocation loc = new MyLocation(row, col);
     * -----------------------------
     * creates a MyLocation object with the given row & col
     * 
     * @param r - row of this MyLocation
     * @param c - column of this MyLocaiton
     */
    public MyLocation(int r, int c)
    {
        row = r;
        col = c;
    }

    /**
     * Getter for the row of the location
     * @return Row of the location
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Getter for the column of the location
     * @return Column of the location
     */
    public int getCol()
    {
        return col;
    }

    /**
     * Checks if this location is equal to another
     * @param other right-hand-side of the comparison
     * @return True if both are MyLocations and this represents
     *         the same location as other
     */
    public boolean equals(Object other)
    {
        return (other instanceof MyLocation rhs) && rhs.row == row && rhs.col == col;
    }

    /**
     * Converts a location to a human-readable string
     * @return A string in the form of "(row, col)"
     */
    public String toString()
    {
        return "(" + row + ", " + col + ")";
    }

    /**
     * Compares a location to another according to the Comparable interface.
     * This will return -1 if x is not a MyLocation!
     * @precondition x is a MyLocation
     * @param x The location to compare against
     * @return 1 if this is greater than x, 0 if equal, -1 if lesser.
     *         The location is said to be greater if the row is greater
     *         or if the rows are equal, the column is greater.
     */
    public int compareTo(Object x)
    {
        if (x instanceof MyLocation rhs)
        {
            if (row < rhs.row)
            {
                return -1;
            }
            else if (rhs.row == row)
            {
                if (rhs.col == col)
                    return 0;
                else if (col < rhs.col)
                    return -1;
            }
        }
        return 1;
    }
}