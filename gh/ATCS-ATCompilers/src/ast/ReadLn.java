package ast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * ReadLn.java
 * @author Grant Yang
 * @version 2024.03.28
 * Implements a ReadLn statement, which reads a line from the console
 * and stores it to an lvalue expression.
 */
public class ReadLn implements Statement
{
    private final Expression expr;

    /**
     * Constructs a new ReadLn statement
     * @param targ The expression to store the read line to
     */
    public ReadLn(Expression targ)
    {
        expr = targ;
    }

    /**
     * Execute the ReadLn statement
     * @param env The environment to execute in
     * @postcondition Input has been read from the command line
     *                and stored in the target expression.
     */
    @Override
    public void exec(Environment env)
    {
        try
        {
            expr.eval(env).set(new BufferedReader(
                    new InputStreamReader(System.in)).readLine());
        }
        catch (Exception err)
        {
            err.printStackTrace();
        }
    }
}
