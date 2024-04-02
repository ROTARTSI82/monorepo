package ast;

public class ReturnException extends RuntimeException
{
    public ReturnException()
    {
        super("RETURN or EXIT must appear in a PROCEDURE");
    }
}
