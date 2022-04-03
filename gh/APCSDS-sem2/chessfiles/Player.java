import java.awt.*;

/**
 * Abstract player for a game of chess
 * @author Grant Yang
 * @version 2022.04.03
 */
public abstract class Player
{
    private Board board;
    private String name;
    private Color color;

    /**
     * Constructs a new player
     * @param board Board to play chess on
     * @param name Name of the player
     * @param color Side of the player, either Color.WHITE or Color.BLACK
     */
    public Player(Board board, String name, Color color)
    {
        this.board = board;
        this.name = name;
        this.color = color;
    }

    /**
     * Getter for the board
     * @return Board to play the game on
     */
    public Board getBoard()
    {
        return board;
    }

    /**
     * Getter for the name of the player
     * @return Player name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Getter for the player's side
     * @return Color.WHITE or Color.BLACK
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Calculates the next move to be played on this player's turn
     * @return The move to play
     */
    public abstract Move nextMove();
}
