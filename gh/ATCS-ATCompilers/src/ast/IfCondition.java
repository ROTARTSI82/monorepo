package ast;

import codegen.Emitter;
import parser.BoxedValue;

/**
 * IfCondition.java
 * An if condition, which executes a statement if a condition is true, and another statement if it is false.
 * The condition is evaluated first, and then the body or else block is executed based on the result.
 *
 * @version 2024.03.28
 * @author Grant Yang
 */
public class IfCondition extends Expression
{
    private static int COUNTER = 0;
    private final int id;
    private final Expression cond, body, elseBlock;

    /**
     * Constructs a new if condition
     * @param cond Condition to evaluate
     * @param body Block to execute if the condition is true
     * @param elseBlock Block to execute if the condition is false
     */
    public IfCondition(Expression cond, Expression body, Expression elseBlock)
    {
        this.id = COUNTER++;
        this.cond = cond;
        this.body = body;
        this.elseBlock = elseBlock;
    }

    /**
     * Evaluate the if condition
     * @param env The environment to evaluate in
     * @return The result of the body or else block, depending on the condition
     */
    @Override
    public BoxedValue eval(Environment env)
    {
        return cond.eval(env).asBool() ? body.eval(env) : elseBlock.eval(env);
    }

    /**
     * Compile the if condition
     * @param emit Object into which to emit the code
     */
    @Override
    public void compile(Emitter emit)
    {
        emit.emit("# begin if condition " + id);
        cond.compile(emit); // stored in $v0

        emit.emit("beqz $v0 elseOf_if" + id);
        emit.emit("if" + id + ":");
        body.compile(emit);
        emit.emit("j continueAfter_if" + id);

        emit.emit("elseOf_if" + id + ":");
        elseBlock.compile(emit);

        emit.emit("continueAfter_if" + id + ":");
    }
}
