import java.awt.Color;
/**
* class BLock encapsulates a Block abstraction which can be placed into a Gridworld style grid
* You are expected to comment this class according to the style guide.
* @author 
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
	* 
    */
    public Color getColor()
    {
        return color;
    }
	/**
	* 
	*/
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
	/**
    * 
    */
    public MyBoundedGrid<Block> getGrid()
    {
        return grid;
    }
    
	/**
	* 
	*/
    public Location getLocation()
    {
        return location;
    }
    
	/**
	* 
	*/
    public void removeSelfFromGrid()
    {
        if (location == null || grid == null)
            throw new RuntimeException("removeSelfFromGrid() called when not in grid");
        grid.remove(location);
        grid = null;
        location = null;
    }
    
	/**
	* 
	*/
    public void putSelfInGrid(MyBoundedGrid<Block> gr, Location loc)
    {
        if (grid != null && location != null)
            removeSelfFromGrid();
        grid = gr;
        location = loc;
        if (grid.get(loc) != null)
            grid.get(loc).removeSelfFromGrid();
        grid.put(location, this);
    }

    /**
	*
	*/
    public void moveTo(Location newLocation)
    {
        if (grid == null)
            throw new RuntimeException("moveTo called on null grid");
        if (location != null)
            grid.remove(location);
        location = newLocation;
        if (grid.get(location) != null)
            grid.get(location).removeSelfFromGrid();
        grid.put(location, this);
    }

    /**
	* returns a string with the location and color of this block
	*/
    public String toString()
    {
        return "Block[location=" + location + ",color=" + color + "]";
    }
}