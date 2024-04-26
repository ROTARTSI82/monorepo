package ast;

import codegen.Emitter;
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
public class ForLoop extends Expression
{
    private final Expression start, stop, body;
    private final int id;

    private static int COUNT = 0;

    /**
     * Constructs a new for loop
     *
     * @param start The starting value of the loop variable
     * @param stop  The stopping value of the loop variable (exclusive).
     * @param body  The code to execute
     */
    public ForLoop(Expression start, Expression stop, Expression body)
    {
        this.start = start;
        this.stop = stop;
        this.body = body;
        this.id = COUNT++;
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
    public BoxedValue eval(Environment env)
    {
        BoxedValue val = start.eval(env);
        BoxedValue max = stop.eval(env);
        while (val.asInt() < max.asInt())
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
            finally
            {
                // this will execute in the case of BREAK too
                val.set(val.asInt() + 1);
            }
        return BoxedValue.NULL;
    }

    @Override
    public void compile(Emitter emit)
    {
        emit.emit("# begin for loop " + id);
        start.compile(emit);
        emit.emit("# for loop " + id);
        emit.emitPush32("$s0");
        emit.emitPush32("$v0");

        stop.compile(emit);
        emit.emitPop32("$t0");

        emit.pushLoopLabel("forLoop" + id);
        emit.emit("forLoop" + id + ":");
        emit.emit("bge $v0 $t0 exit_forLoop" + id);

        body.compile(emit);

        emit.emitPop32("$s0");
        emit.emit("lw $t0 ($s0)");
        emit.emit("addi $t0 $t0 1");
        emit.emit("sw $t0 ($s0)");
        emit.emitPush32("$s0");
        emit.emit("j forLoop" + id);

        emit.emit("exit_forLoop" + id + ":");
        emit.popLoopLabel();
        emit.emit("# end for loop " + id);
    }
}
