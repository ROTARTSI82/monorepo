/**
 * Class implementing the main logic for the Tetris game
 * @author Grant Yang
 * @version 2022.03.15
 */
public class Tetris implements ArrowListener
{
    private Tetrad active;
    private final BlockDisplay disp;
    private final MyBoundedGrid<Block> grid;

    /**
     * Determines the type of the next block to be dropped
     * Integer in [0, 7). See Tetrad documentation for tetromino types
     */
    public int next = (int) (Math.random() * 7);

    private double speed = 0;

    /**
     * The current level of the tetris game
     */
    public int level = 0;

    /**
     * Counter for the number of lines cleared
     */
    public int linesCleared = 0;

    /**
     * Tracks the number of each type we have seen so far
     */
    public int[] stats = new int[7];

    /**
     * Score of the player
     */
    public int score = 0;

    /**
     * Constructs a new tetris game (this opens a window)
     */
    public Tetris()
    {
        grid = new MyBoundedGrid<Block>(20 + 2, 10);
        active = new Tetrad(grid, next);
        next = (int) (Math.random() * 7);

        stats[active.getType()]++;

        disp = new BlockDisplay(grid);
        disp.setTetris(this);
        disp.setTitle("Tetris");
        disp.showBlocks();

    }

    /**
     * Plays a new game of tetris
     * @param args Command line arguments
     */
    public static void main(String[] args)
    {
        new Tetris().play();
    }

    /**
     * Checks if a certain row is completed
     * precondition:  0 <= row < number of rows
     * postcondition: Returns true if every cell in the
     *                given row is occupied;
     *                returns false otherwise.
     * @param row The row number to check
     * @return True if the row is completely filled, false otherwise.
     */
    private boolean isCompletedRow(int row)
    {
        Location cur = new Location(row, 0);
        while (grid.isValid(cur))
            if (grid.get(cur) == null)
                return false;
            else
                cur = cur.getAdjacentLocation(Location.EAST);
        return true;
    }

    /**
     * Clears a row, moving the rows above it down
     * precondition:  0 <= row < number of rows;
     *                given row is full of blocks
     * postcondition: Every block in the given row has been
     *                removed, and every block above row
     *                has been moved down one row.
     * @param row Row to clear and move things above down
     */
    private void clearRow(int row)
    {
        linesCleared++;

        Location cur = new Location(row, 0);
        while (grid.isValid(cur))
        {
            if (grid.get(cur) != null)
                grid.get(cur).removeSelfFromGrid();
            Location above = cur.getAdjacentLocation(Location.NORTH);

            while (grid.isValid(above))
            {
                if (grid.get(above) != null)
                    grid.get(above).moveTo(above.getAdjacentLocation(Location.SOUTH));
                above = above.getAdjacentLocation(Location.NORTH);
            }

            cur = cur.getAdjacentLocation(Location.EAST);
        }
    }

    /**
     * Scans for completed rows and clears them
     * postcondition: All completed rows have been cleared.
     */
    private void clearCompletedRows()
    {
        for (int row = 1; row < 22; row++)
            if (isCompletedRow(row))
                clearRow(row);
    }

    /**
     * Resets the game of tetris, clearing the grid and
     * statistics like score, level, and lines cleared
     */
    public void reset()
    {
        speed = 0;
        level = 0;
        for (int i = 0; i < 7; i++) stats[i] = 0;
        linesCleared = 0;
        score = 0;

        Location loc = new Location(0, 0);
        while (grid.isValid(loc))
        {
            Location cpy = new Location(loc.getRow(), loc.getCol());
            while (grid.isValid(cpy))
            {
                grid.remove(cpy);
                cpy = cpy.getAdjacentLocation(Location.EAST);
            }

            loc = loc.getAdjacentLocation(Location.SOUTH);
        }

        active = new Tetrad(grid, next);
        next = (int) (Math.random() * 7);

    }

    /**
     * Plays a game of tetris, running the main game logic loop,
     * which makes blocks fall and clears the screen
     */
    public void play()
    {
        for (;;)
            try
            {
                speed = 50 + 1750.0 / (1.0 + Math.exp(level / 5.0));
                Thread.sleep((long) speed);
                if (!active.translate(1, 0))
                {
                    active = null;

                    int oldLines = linesCleared;
                    clearCompletedRows();

                    active = new Tetrad(grid, next);
                    next = (int) (Math.random() * 7);

                    stats[active.getType()]++;

                    int dlines = linesCleared - oldLines;

                    score += level + 64 + dlines * (level + 1) * 160;
                    level = linesCleared / 8;

                }

                disp.showBlocks();
            }
            catch(Tetrad.GameOver over) 
            {
                disp.gameOver();
                disp.showBlocks();

                while (disp.state != BlockDisplay.State.TETRIS)
                    try
                    {
                        Thread.sleep(125);
                    }
                    catch (Throwable e)
                    {
                        //;
                    }
                play();
            }
            catch(InterruptedException e)
            {
                //ignore
            }
    }

    /**
     * When the A key is pressed (NOT the up arrow),
     * rotate the block
     */
    @Override
    public void upPressed()
    {
        score -= Math.max(speed / 100, 1);
        active.rotate();
        disp.showBlocks();
    }

    /**
     * When the down arrow is pressed, move the block down faster.
     */
    @Override
    public void downPressed()
    {
        score += (1 + level) * 2;
        active.translate(1, 0);
        disp.showBlocks();
    }

    /**
     * When the left arrow is pressed, move the block left
     */
    @Override
    public void leftPressed()
    {
        score -= Math.max(speed / 100, 1);
        active.translate(0,  -1);
        disp.showBlocks();
    }

    /**
     * When the right arrow is pressed, move the block right
     */
    @Override
    public void rightPressed()
    {
        score -= Math.max(speed / 100, 1);
        active.translate(0, 1);
        disp.showBlocks();
    }

    /**
     * Unused callback function listening for presses of the R key
     */
    @Override
    public void rPressed()
    {
    }

    /**
     * Instantly drop a block to the bottom with space bar.
     */
    @Override
    public void instaDrop()
    {
        while (active.translate(1, 0))
            score += (1 + level) * 3;

        disp.showBlocks();
    }
}
