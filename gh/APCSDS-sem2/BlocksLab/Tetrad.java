import java.awt.*;
import java.util.concurrent.Semaphore;

/**
 * Tetrad class represents a tetromino of four blocks, with methods
 * for transformations.
 * @author Grant Yang
 * @version 2021.03.15
 */
public class Tetrad
{
    /**
     * Game over exception for control flow in the play() loop
     */
    public static class GameOver extends RuntimeException {}

    /**
     * Data for the coordinates of all tetrominoes and their
     * centers of rotation. All coordinates are in row, col.
     * Indexed by tetromino type
     */
    public static final int[][] LOCS = {
            { 0, 0,  0, 1,  0, 2,  0, 3,  0, 1 },
            { 0, 0,  1, 0,  2, 0,  1, 1,  1, 0 },
            { 0, 0,  1, 0,  0, 1,  1, 1,  -1, -1 }, // o - Explicit case for no rotation
            { 0, 0,  0, 1,  0, 2,  1, 2,  0, 1 },
            { 0, 2,  1, 0,  1, 1,  1, 2,  1, 1 },
            { 1, 0,  2, 0,  0, 1,  1, 1,  1, 0 },
            { 0, 0,  1, 0,  1, 1,  2, 1,  1, 0 }
    };

    /**
     * The colors for all tetrominoes, indexed by their type integer
     */
    public static final Color[] COLS = {Color.RED, Color.GRAY, Color.CYAN, Color.YELLOW,
            Color.PINK, Color.BLUE, Color.GREEN};

    private final Block[] blocks = new Block[4];
    private Location center;
    private MyBoundedGrid<Block> grid;
    private final int blockType;
    private final Semaphore lock = new Semaphore(1, true);

    /**
     * Transformation interface for passing lambdas of applying transformations
     * to individual Location coordinates about a center
     */
    interface Transformation
    {
        /**
         * Apply an arbitrary transformation
         * @param loc Location to transform
         * @param center Center to transform about
         * @return New transformed Location
         */
        Location transform(Location loc, Location center);
    }

    /**
     * Constructs a new tetrad of a specified type
     * @param grid The grid to put tetrad into
     * @param type Integer in [0, 7); See getType() documentation for full details
     */
    public Tetrad(MyBoundedGrid<Block> grid, int type)
    {
        blockType = type;



        for (int i = 0; i < 4; i++)
        {
            blocks[i] = new Block();
            blocks[i].setColor(COLS[blockType]);
        }

        Location[] realLocs = new Location[4];

        for (int i = 0; i < 4; i++)
        {
            realLocs[i] = new Location(2 + LOCS[blockType][i * 2 + 1], 4 + LOCS[blockType][i * 2]);
            if (grid.get(realLocs[i]) != null) throw new GameOver();
        }

        center = new Location(2 + LOCS[blockType][9], 4 + LOCS[blockType][8]);

        addToLocations(grid, realLocs);
    }

    /**
     * Gets the integral block type.
     * 0 -> I
     * 1 -> T
     * 2 -> O
     * 3 -> L
     * 4 -> J
     * 5 -> S
     * 6 -> Z
     * @return An integer in [0, 7)
     */
    public int getType()
    {
        return blockType;
    }

    /**
     * Adds the blocks of this tetrad to the specified grid
     * at the specified locations
     * @param gr Grid to put blocks into
     * @param locs Array of 4 distinct locations to put the blocks into
     * precondition:  blocks are not in any grid;
     *                locs.length = 4.
     * postcondition: The locations of blocks match locs,
     *                and blocks have been put in the grid.
     */
    private void addToLocations(MyBoundedGrid<Block> gr, Location[] locs)
    {
        this.grid = gr;
        for (int i = 0; i < 4; i++)
            blocks[i].putSelfInGrid(gr, locs[i]);
    }

    /**
     * Removes all blocks and returns their locations
     * precondition:  Blocks are in the grid.
     * postcondition: Returns old locations of blocks;
     *                blocks have been removed from grid.
     * @return An array of 4 distinct locations in which the blocks had been
     */
    private Location[] removeBlocks()
    {
        Location[] ret = new Location[4];
        for (int i = 0; i < 4; i++)
        {
            ret[i] = blocks[i].getLocation();
            blocks[i].removeSelfFromGrid();
        }

        return ret;
    }

    /**
     * Checks if ALL locations are valid and empty within a grid
     * postcondition: Returns true if each of locs is
     *                valid and empty in grid;
     *                false otherwise.
     * @param gr Grid to check locations in
     * @param locs List of 4 locations to check
     * @return True if all locations are empty, false otherwise
     */
    private boolean areEmpty(MyBoundedGrid<Block> gr, Location[] locs)
    {
        for (int i = 0; i < 4; i++)
            if (!(gr.isValid(locs[i]) && gr.get(locs[i]) == null))
                return false;
        return true;
    }

    /**
     * Attempts to translate the tetromino by an arbitrary amount
     * postcondition: Attempts to move this tetrad deltaRow
     *                rows down and deltaCol columns to the
     *                right, if those positions are valid
     *                and empty; returns true if successful
     *                and false otherwise.
     * @param deltaRow Difference between the desired new row and the current row
     * @param deltaCol Difference between the desired new column and the current column
     * @return True if the tetromino was successfully translated,
     *         false if its final destination was obstructed.
     */
    public boolean translate(int deltaRow, int deltaCol)
    {
        return transform((l, _c) -> new Location(l.getRow() + deltaRow, l.getCol() + deltaCol));
    }

    /**
     * Attempt to rotate the tetromino about its center
     * @return True if the tetromino was rotated successfully,
     *         false if it was obstructed and was unable to be rotated.
     */
    public boolean rotate()
    {
        if (blockType == 2) return true; // o block
        return transform((l, c) -> new Location(c.getRow() - c.getCol() + l.getCol(),
                c.getRow() + c.getCol() - l.getRow()));
    }

    /**
     * Attempts to apply an arbitrary transformation to the tetromino.
     * This method is thread-safe and uses a mutex
     * @param trans Transformation to apply
     * @return True if the transformation was applied successfully,
     *         false otherwise.
     */
    public boolean transform(Transformation trans)
    {
        try
        {
            lock.acquire();
            Location[][] c = new Location[4][2];
            for (int i = 0; i < 4; i++)
            {
                c[i][0] = blocks[i].getLocation();
                c[i][1] = trans.transform(c[i][0], center);

                blocks[i].removeSelfFromGrid();
            }

            boolean ret = true;
            for (int i = 0; i < 4; i++)
                ret = ret && grid.isValid(c[i][1]) && grid.get(c[i][1]) == null;

            for (int i = 0; i < 4; i++)
                blocks[i].putSelfInGrid(grid, c[i][ret ? 1 : 0]);
            if (ret)
                center = trans.transform(center, center);
            return ret;
        }
        catch (Throwable e)
        {
            return false;
        }
        finally
        {
            lock.release();
        }
    }

}
