/**
 * Represents a single move, in which a piece moves to a destination location.
 * Since a move can be undone, also keeps track of the source location and any captured victim.
 * @author Unknown
 * @version 2022.05.01
 */
public class Move
{
    private Piece piece;          //the piece being moved
    private Location source;      //the location being moved from
    private Location destination; //the location being moved to
    private Piece victim;         //any captured piece at the destination

    /**
     * Constructs a new move for moving the given piece to the given destination.
     * @param piece Piece to move
     * @param destination Location to move the piece to
     */
    public Move(Piece piece, Location destination)
    {
        this.piece = piece;
        this.source = piece.getLocation();
        this.destination = destination;
        this.victim = piece.getBoard().get(destination);

        if (source.equals(destination))
            throw new IllegalArgumentException("Both source and dest are " + source);
    }

    /**
     * Getter for the piece to move
     * @return Returns the piece being moved
     */
    public Piece getPiece()
    {
        return piece;
    }

    /**
     * Getter for the source location
     * @return Returns the location being moved from
     */
    public Location getSource()
    {
        return source;
    }

    /**
     * Getter for the piece destination
     * @return Returns the location being moved to
     */
    public Location getDestination()
    {
        return destination;
    }

    /**
     * Getter for the captured piece
     * @return Returns the piece being captured at the destination, if any
     */
    public Piece getVictim()
    {
        return victim;
    }

    /**
     * Constructs a numan readable string of the move
     * @return Returns a string description of the move
     */
    public String toString()
    {
        return piece + " from " + source + " to " + destination + " containing " + victim;
    }

    /**
     * Compare two moves
     * @param x The move to compare this to
     * @return Returns true if this move is equivalent to the given one.
     */
    public boolean equals(Object x)
    {
        Move other = (Move) x;
        return piece == other.getPiece() && source.equals(other.getSource()) &&
                destination.equals(other.getDestination()) && victim == other.getVictim();
    }

    /**
     * Hashes this move
     * @return Returns a hash code for this move,
     *         such that equivalent moves have the same hash code.
     */
    public int hashCode()
    {
        return piece.hashCode() + source.hashCode() + destination.hashCode();
    }
}