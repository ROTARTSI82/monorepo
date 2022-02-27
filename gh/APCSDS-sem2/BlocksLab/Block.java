import java.awt.Color;
/**
 * class BLock encapsulates a Block abstraction which can be placed into a Gridworld style grid
 *
 * @author Grant Yang
 * @version 2022.02.26
 */
public class Block
{
    private MyBoundedGrid<Block> grid;
    private Location location;
    private Color color;

    /**
     * constructs a blue block, because blue is the greatest color ever!
     */
    public Block()
    {
        color = Color.BLUE;
        grid = null;
        location = null;
    }

    /**
     * Getter method for the color of the block
     * @return java.awt.Color of the block
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Setter for the color of the block
     * @param newColor New color to set the block to
     */
    public void setColor(Color newColor)
    {
        color = newColor;
    }

    /**
     * Getter for the grid this block is currently in
     * @return The grid which contains this block, null if there is none.
     */
    public MyBoundedGrid<Block> getGrid()
    {
        return grid;
    }

    /**
     * Getter for the location of this block within the grid
     * @return The location of the block, null if the block is not in a grid.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Removes this block from the grid its in, updating both
     * the grid's and this block's pointers.
     * @precondition The block is in a grid
     * @throws RuntimeException if the precondition is not met
     * @postcondition This block is no longer in any grid
     */
    public void removeSelfFromGrid()
    {
        if (grid == null)
            throw new RuntimeException("removeSelfFromGrid() called when not in grid");
        grid.remove(location);
        grid = null;
        location = null;
    }

    /**
     * Places this block into a new grid, removing it from
     * the previous grid it was in. This method would replace
     * any value currently occupying the specified location and properly update
     * the previous value by calling removeSelfFromGrid() on it.
     * @precondition gr and loc are not null, and loc is a valid location
     *               in the specified grid.
     * @throws RuntimeException if the precondition is not met
     * @postcondition The grid contains this block at the specified location
     *                and this block tracks where it is.
     * @param gr The grid to place in
     * @param loc Location to place the block in
     */
    public void putSelfInGrid(MyBoundedGrid<Block> gr, Location loc)
    {
        if (gr == null || loc == null)
            throw new RuntimeException("Cannot putSelfInGrid on a null grid");
        if (grid != null && location != null)
            removeSelfFromGrid();
        grid = gr;
        location = loc;
        if (grid.get(loc) != null)
            grid.get(loc).removeSelfFromGrid();
        grid.put(location, this);
    }

    /**
     * Moves the block to a new location, removing the block from
     * its old location and putting it in a new location, which
     * may or may not be occupied. In the case that the new location is occupied,
     * the Block there is properly removed with removeSelfFromGrid()
     * @precondition This Block is in a Grid
     * @throws RuntimeException if the precondition is not met
     * @postcondition The old location of the block is empty and the
     *                specified location contains this block
     * @param newLocation The new location to move the block to
     */
    public void moveTo(Location newLocation)
    {
        if (grid == null)
            throw new RuntimeException("moveTo called on null grid");
        grid.remove(location);
        location = newLocation;
        if (grid.get(location) != null)
            grid.get(location).removeSelfFromGrid();
        grid.put(location, this);
    }

    /**
     * Constructs a human-readable string representation of this block
     * @return A string with the location and color of this block
     */
    public String toString()
    {
        return "Block[location=" + location + ",color=" + color + "]";
    }
}