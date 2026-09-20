import java.awt.*;

/**
 * An alpha-beta searcher with a depth of 5 (searching 6 plies ahead)
 * @author Grant Yang
 * @version 2022.04.03
 */
public class SmartestPlayer extends SmartPlayer
{
    /**
     * Constructs a new SmartestPlayer
     * @param b Board to play chess on
     * @param n Name of the player
     * @param c Side, either Color.BLACK or Color.WHITE
     */
    SmartestPlayer(Board b, String n, Color c)
    {
        super(b, n, c, 5);
    }
}
