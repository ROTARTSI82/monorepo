package ast;

import parser.BoxedValue;

/**
 * WhileLoop.java
 * Implements a while loop, with special exception handling
 * to implement CONTINUE and BREAK
 *
 * @author Grant Yang
 * @version 2024.03.21
 */
public class WhileLoop implements Expression
{
    private final Expression cond, body;

    /**
     * Constructs a new while loop
     *
     * @param cond The expression to check for the condition to continue looping.
     * @param body The code to execute
     */
    public WhileLoop(Expression cond, Expression body)
    {
        this.cond = cond;
        this.body = body;
    }

    /**
     * Execute the while loop, calling exec() on the body
     * statement until cond becomes false or the body throws
     * BreakException.
     *
     * @param env Environment to execute in
     */
    @Override
    public BoxedValue eval(Environment env)
    {
        while (cond.eval(env).asBool())
            try
            {
                body.eval(env);
            }
            catch (BreakException b)
            {
                return BoxedValue.NULL;
            }
            catch (ContinueException ignored)
            {
            }
        return BoxedValue.NULL;
    }
}
