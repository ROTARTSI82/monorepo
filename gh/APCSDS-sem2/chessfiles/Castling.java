public class Castling extends Move
{
    private final Move rookMove;

    public Castling(Piece piece, Location destination, Move rookMove)
    {
        super(piece, destination);
        this.rookMove = rookMove;
    }

    public Move getRookMove()
    {
        return rookMove;
    }
}
