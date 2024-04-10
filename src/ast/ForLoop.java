package ast;

import parser.BoxedValue;

/**
 * ForLoop.java
 *
 * @author Grant Yang
 * @version 2024.03.28
 * A for loop, which executes a statement a certain number of times.
 * The loop takes the loop variable from its starting value to
 * one less than the stopping value (it is exclusive).
 * For example, `FOR i := 0 TO 10 DO` will execute the body 10 times.
 */
public class ForLoop implements Statement
{
    private final Expression start, stop;
    private final Statement body;

    /**
     * Constructs a new for loop
     *
     * @param start The starting value of the loop variable
     * @param stop  The stopping value of the loop variable (exclusive).
     * @param body  The code to execute
     */
    public ForLoop(Expression start, Expression stop, Statement body)
    {
        this.start = start;
        this.stop = stop;
        this.body = body;
    }

    /**
     * Execute the for loop, calling exec() on the body until the stopping condition is met.
     *
     * @param env The environment to execute in
     * @postcondition `start` and `stop` have each been evaluated once,
     * and `body` has been executed until either a BREAK was reached
     * or the loop variable reached the value of `stop`.
     */
    @Override
    public void exec(Environment env)
    {
        BoxedValue val = start.eval(env);
        BoxedValue max = stop.eval(env);
        while (val.asInt() < max.asInt())
            try
            {
                body.exec(env);
            }
            catch (BreakException b)
            {
                return;
            }
            catch (ContinueException ignored)
            {
            }
            finally
            {
                // this will execute in the case of BREAK too
                val.set(val.asInt() + 1);
            }
    }
}
