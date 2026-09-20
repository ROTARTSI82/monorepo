package ast;

import codegen.Emitter;
import parser.BoxedValue;

/**
 * WhileLoop.java
 * Implements a while loop, with special exception handling
 * to implement CONTINUE and BREAK
 *
 * @author Grant Yang
 * @version 2024.03.21
 */
public class WhileLoop extends Expression
{
    private static int COUNT = 0;
    private final int id;
    private final Expression cond, body;

    /**
     * Constructs a new while loop
     *
     * @param cond The expression to check for the condition to continue looping.
     * @param body The code to execute
     */
    public WhileLoop(Expression cond, Expression body)
    {
        id = COUNT++;
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

    /**
     * Compile the while loop into assembly
     * @param emit Object into which to emit the code
     */
    @Override
    public void compile(Emitter emit)
    {
        emit.emit("# begin while loop " + id);
        emit.pushLoopLabel("whileLoop" + id);
        emit.emit("whileLoop" + id + ":");
        cond.compile(emit);
        emit.emit("beqz $v0 exit_whileLoop" + id);

        body.compile(emit);
        emit.emit("j whileLoop" + id);

        emit.emit("exit_whileLoop" + id + ":");
        emit.popLoopLabel();
        emit.emit("# end while loop " + id);
    }
}
