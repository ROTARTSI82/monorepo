package ast;

/**
 * BreakException.java
 * @author Grant Yang
 * @version 2024.03.25
 * An exception to be thrown when a BREAK; statement is encountered.
 * This exception is caught by any loops that the BREAK; acts on.
 */
public class BreakException extends RuntimeException
{
    public BreakException()
    {
        super("BREAK must be in a FOR or WHILE loop");
    }
}
