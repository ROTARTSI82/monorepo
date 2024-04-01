package ast;

/**
 * ContinueException.java
 * @author Grant Yang
 * @version 2024.03.25
 * An exception to be thrown when a CONTINUE; statement is encountered.
 * This exception is caught by any loops that the CONTINUE; acts on.
 */
public class ContinueException extends RuntimeException
{
    public ContinueException()
    {
        super("CONTINUE must be in a FOR or WHILE loop");
    }
}
