import java.net.URL;
import java.util.Objects;

/**
 * Playing card class
 * @author Grant Yang
 * @version 2021.11.03
 */
public class Card
{
    /**
     * Construct a card of the given suit and rank
     * @param rank The rank of the card. 1 = ace, 13 = king
     * @param suit Suit. 0=clubs, 1=diamonds, 2=spades, 3=hearts
     */
    public Card(int rank, int suit)
    {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Gets the integral rank
     * @return 1 for ace, 13 for king, etc.
     */
    public int getRank()
    {
        return rank;
    }

    /**
     * Returns the string representation of the suit
     * @return Suit name in all lowercase followed by a space and unicode representation.
     */
    public String getSuit()
    {
        switch (suit)
        {
            case 0: return "clubs ♣";
            case 1: return "diamonds ♦";
            case 2: return "spades ♠";
            default: return "hearts ♥";
        }
    }

    /**
     * Queries the color of the card suit
     * @return True if the card is diamonds or hearts.
     */
    public boolean isRed()
    {
        return suit % 2 == 1;
    }

    /**
     * Queries the face-up property of the card.
     * All cards start face-down.
     * @return True if the card is facing up and thus visible.
     */
    public boolean isFaceUp()
    {
        return isUp;
    }

    /**
     * Turns to card to face up
     */
    public void turnUp()
    {
        isUp = true;
    }

    /**
     * Turns the card to face down
     */
    public void turnDown()
    {
        isUp = false;
    }

    private URL loadResource(String p)
    {
        return Objects.requireNonNull(Card.class.getClassLoader().
                getResource(p));
    }

    /**
     * Gets the image fine to use for this card's icon depending on
     * its rank, suit, and whether it is facing up.
     * @return A filepath to the appropriate image for the card
     */
    public URL getFileName()
    {
        if (!isUp) return loadResource("cards/backapcsds.gif");
        return loadResource(String.format("cards/%c%c.gif", LOOKUP.charAt(rank - 1),
                getSuit().charAt(0)));
    }

    /**
     * Converts this card to a human-readable string representation
     * @return {rank} of {suit} (e.g. "3 of spades ♠")
     */
    @Override
    public String toString()
    {
        return getRank() + " of " + getSuit();
    }

    private boolean isUp = false;
    private final int rank;
    private final int suit; // [0, 3]
    
    private static final String LOOKUP = "a23456789tjqk";

    public Card copy()
    {
        Card clone = new Card(rank, suit);
        clone.isUp = isUp;
        return clone;
    }
}
