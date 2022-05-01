/**
 * Special castling move, which is represented as two separate normal moves
 * @author Grant Yang
 * @version 2022.05.1
 */
public class Castling extends Move
{
    private final Move rookMove;

    /**
     * Makes a new castling move
     * @param piece The king
     * @param destination Where to move the king after castling
     * @param rookMove How to move the rook
     */
    public Castling(Piece piece, Location destination, Move rookMove)
    {
        super(piece, destination);
        this.rookMove = rookMove;
    }

    /**
     * Getter for the rook move
     * @return The rookMove passed into the constructor
     */
    public Move getRookMove()
    {
        return rookMove;
    }
}
