import java.awt.*;

public abstract class Player
{
    private Board board;
    private String name;
    private Color color;

    public Player(Board board, String name, Color color)
    {
        this.board = board;
        this.name = name;
        this.color = color;
    }

    public Board getBoard()
    {
        return board;
    }

    public String getName()
    {
        return name;
    }

    public Color getColor()
    {
        return color;
    }

    public abstract Move nextMove();
}
