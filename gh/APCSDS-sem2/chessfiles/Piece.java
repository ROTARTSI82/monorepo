import java.awt.*;
import java.util.*;

/**
 * An abstract Piece in a chess game (e.g. pawn, rook, queen, etc)
 * @author Grant Yang
 * @version 2022.04.03
 */
public abstract class Piece
{
    //the board this piece is on
    private Board board;

    //the location of this piece on the board
    private Location location;

    //the color of the piece
    private Color color;

    //the file used to display this piece
    private String imageFileName;

    //the approximate value of this piece in a game of chess
    private int value;

    /**
     * constructs a new Piece with the given attributes
     * @param col Color, either Color.WHITE or Color.BLACK
     * @param fileName File of the image to use for the piece
     * @param val Point value of the piece in pawns
     */
    public Piece(Color col, String fileName, int val)
    {
        color = col;
        if (col.equals(Color.WHITE))
            imageFileName = "white" + fileName;
        else imageFileName = "black" + fileName;
        value = val;
    }

    /**
     * returns the board this piece is on
     * @return The board object this piece is on
     */
    public Board getBoard()
    {
        return board;
    }

    /**
     * returns the location of this piece on the board
     * @return The Location of this piece
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * returns the color of this piece
     * @return Color of the piece
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * returns the name of the file used to display this piece
     * @return The path of the file used for the image
     */
    public String getImageFileName()
    {
        return imageFileName;
    }

    /**
     * returns a number representing the relative value of this piece
     * @return Value of this piece in pawns
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Puts this piece into a board. If there is another piece at the given
     * location, it is removed. <br />
     * Precondition: (1) This piece is not contained in a grid (2)
     * <code>loc</code> is valid in <code>gr</code>
     * @param brd the board into which this piece should be placed
     * @param loc the location into which the piece should be placed
     */
    public void putSelfInGrid(Board brd, Location loc)
    {
        if (board != null)
            throw new IllegalStateException(
                    "This piece is already contained in a board.");

        Piece piece = brd.get(loc);
        if (piece != null)
            piece.removeSelfFromGrid();
        brd.put(loc, this);
        board = brd;
        location = loc;
    }

    /**
     * Removes this piece from its board. <br />
     * Precondition: This piece is contained in a board
     */
    public void removeSelfFromGrid()
    {
        if (board == null)
            throw new IllegalStateException(
                    "This piece is not contained in a board.");
        if (board.get(location) != this)
            throw new IllegalStateException(
                    "The board contains a different piece at location "
                            + location + ".");

        board.remove(location);
        board = null;
        location = null;
    }

    /**
     * Moves this piece to a new location. If there is another piece at the
     * given location, it is removed. <br />
     * Precondition: (1) This piece is contained in a grid (2)
     * <code>newLocation</code> is valid in the grid of this piece
     * @param newLocation the new location
     */
    public void moveTo(Location newLocation)
    {
        if (board == null)
            throw new IllegalStateException("This piece is not on a board.");
        if (board.get(location) != this)
            throw new IllegalStateException(
                    "The board contains a different piece at location "
                            + location + ".");
        if (!board.isValid(newLocation))
            throw new IllegalArgumentException("Location " + newLocation
                    + " is not valid.");

        if (newLocation.equals(location))
            return;
        board.remove(location);
        Piece other = board.get(newLocation);
        if (other != null)
            other.removeSelfFromGrid();
        location = newLocation;
        board.put(location, this);
    }

    /**
     * Checks if a location is a valid destination for the piece
     * @param dest Location to check
     * @return True if it is a valid Location on the board that is
     *         either empty or contains a piece of the opposite color.
     */
    public boolean isValidDestination(Location dest)
    {
        return board.isValid(dest) &&
               (board.get(dest) == null || !board.get(dest).getColor().equals(color));
    }

    /**
     * Function for getting the valid destination Locations
     * for the valid moves of this piece
     * @return An array of the possible destinations
     */
    public abstract ArrayList<Location> destinations();

    /**
     * Sweeps in a direction, adding all contiguous locations in that direction
     * to the array
     * @param dests Array to add the locations
     * @param direction Direction to cast a ray in, a constant from the Location class.
     */
    public void sweep(ArrayList<Location> dests, int direction)
    {
        for (Location cur = getLocation().getAdjacentLocation(direction);
                isValidDestination(cur);
                cur = cur.getAdjacentLocation(direction))
        {
            dests.add(cur);
            if (getBoard().get(cur) != null) return;
        }
    }
}