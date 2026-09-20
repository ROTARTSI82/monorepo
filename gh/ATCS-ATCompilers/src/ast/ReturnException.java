package ast;

/**
 * ReturnException.java
 *
 * @author Grant Yang
 * @version 2024.04.10
 * An exception to be thrown when a RETURN or EXIT statement is encountered.
 * This exception is caught by the environment to return from a procedure.
 */
public class ReturnException extends RuntimeException
{
    public ReturnException()
    {
        super("RETURN or EXIT must appear in a PROCEDURE");
    }
}
