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
     *
     * @param len
     * @param w
     */
    public Rectangle(int len, int w)
    {
        length = len;
        width = w;
    }

    /**
     *
     * @return
     */
    public int getLength()
    {
        return length;
    }

    /**
     *
     * @return
     */
    public int getWidth()
    {
        return width;
    }

    /**
     *
     * @return
     */
    public String toString()
    {
        return length + "x" + width;
    }

    /**
     *
     * @param rhs
     * @return
     */
    @Override
    public boolean equals(Object rhs)
    {
        // This is called a "Pattern Variable." See https://openjdk.java.net/jeps/394
        return rhs instanceof Rectangle rect && rect.length == length && rect.width == width;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode()
    {
        return 0x8125bf11 ^ (length << 8) ^ width ^ (length + width) << 16 ^ (length * width) << 24;
    }

}