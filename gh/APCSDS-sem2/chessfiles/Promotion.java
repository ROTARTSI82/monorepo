public class Promotion extends Move
{
    private final Class<? extends Piece> type;

    public Promotion(Piece piece, Location destination, Class<? extends Piece> type)
    {
        super(piece, destination);
        this.type = type;
    }

    public Class<? extends Piece> getType()
    {
        return type;
    }
}
