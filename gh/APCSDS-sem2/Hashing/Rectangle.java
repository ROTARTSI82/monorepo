/**
 * Rectangle represented by its dimensions as
 * a pair of integer lengths.
 *
 * @author Grant Yang
 * @version 2022.01.21
 */
public class Rectangle
{
    private final int length;
    private final int width;

    /**
     * Constructs a new rectangle with specific dimensions
     * @param len Length of rectangle
     * @param w Width of rectangle
     */
    public Rectangle(int len, int w)
    {
        length = len;
        width = w;
    }

    /**
     * Getter method for the length
     * @return Length of the rectangle
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Getter method for the width
     * @return Width of the rectangle
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Converts the rectangle to a human-readable string
     * @return A string in the format of "<length>x<width>"
     */
    public String toString()
    {
        return length + "x" + width;
    }

    /**
     * Checks if this this rectangle is equal to another object
     * @param rhs Object to compare against
     * @return True if `this` and `rhs` are rectangles with the
     *         same dimensions, false otherwise.
     */
    @Override
    public boolean equals(Object rhs)
    {
        // This is called a "Pattern Variable." See https://openjdk.java.net/jeps/394
        return rhs instanceof Rectangle rect && rect.length == length && rect.width == width;
    }

    /**
     * Hashes this rectangle to a 32-bit hash code in O(1)
     * @return Hash code for this rectangle, to be used by HashMap/HashSet
     *         containers.
     */
    @Override
    public int hashCode()
    {
        return 0x8125bf11 ^ (length << 15) ^ width;
    }

}