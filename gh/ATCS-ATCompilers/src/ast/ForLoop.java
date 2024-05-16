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

    /**
     * Compile the for loop into MIPS assembly.
     * This implementation currently does not play well with RETURN/EXIT,
     * and using a RETURN or EXIT in a for loop WILL corrupt your stack!
     * @param emit Codegen object to use
     */
    @Override
    public void compile(Emitter emit)
    {
        /*
         * todo: this for loop implementation is very broken, it needs to be fixed
         *       if there is a RETURN or EXIT in the middle, it will corrupt the stack
         *       and break all your code!!!!
         */
        emit.emit("# begin for loop " + id);
        emit.emit("# WARNING, not impl: current FOR impl corrupts the stack on RETURN/EXIT");
        start.compile(emit);
        if (!start.getType(emit).equals(Type.Int) || !stop.getType(emit).equals(Type.Int))
            throw new RuntimeException("for loop var must be int");

        emit.emit("# for loop " + id);
        emit.emit("push $s0");
        System.out.println("for sp " + emit.sp);
        emit.emit("push $v0");

        stop.compile(emit);
        emit.emit("pop $t0"); // t0 = value of start, v0 = value of stop
        emit.emit("push $v0");
        emit.pushLoopLabel("forLoop" + id);
        emit.emit("forLoop" + id + ":");
        emit.emit("bge $t0 $v0 exit_forLoop" + id);

        body.compile(emit);

        // lmao double pointer deref is funny
        System.out.println("for sp end " + emit.sp);

        emit.emit("# for loop " + id + " update loop variable");
        emit.emit("lw $s0 8($sp)");
        emit.emit("lw $v0 4($sp)");
        emit.emit("lw $t0 ($s0)");
        emit.emit("addi $t0 $t0 1");
        emit.emit("sw $t0 ($s0)");
        emit.emit("j forLoop" + id);

        emit.emit("exit_forLoop" + id + ":");
        emit.emit("addi $sp $sp 8"); // pop $v0 $s0
        emit.sp -= 8;
        emit.popLoopLabel();
        emit.emit("# end for loop " + id);
    }
}
