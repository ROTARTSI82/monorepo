/**
 * Promotion move where a pawn can become a queen
 * @author Grant Yang
 * @version 2022.05.1
 */
public class Promotion extends Move
{
    private final Class<? extends Piece> type;

    /**
     * Creates a new promotion move
     * @param piece The pawn to promote
     * @param destination Where to move the pawn
     * @param type What to promote to, e.g. "Queen.class"
     */
    public Promotion(Piece piece, Location destination, Class<? extends Piece> type)
    {
        super(piece, destination);
        this.type = type;
    }

    /**
     * Getter for the promotion type
     * @return The class of the piece to promote to
     */
    public Class<? extends Piece> getType()
    {
        return type;
    }
}
