package ast;

import codegen.Emitter;
import parser.BoxedValue;

public class IfCondition extends Expression
{
    private static int COUNTER = 0;
    private final int id;
    private final Expression cond, body, elseBlock;

    public IfCondition(Expression cond, Expression body, Expression elseBlock)
    {
        this.id = COUNTER++;
        this.cond = cond;
        this.body = body;
        this.elseBlock = elseBlock;
    }
    @Override
    public BoxedValue eval(Environment env)
    {
        return cond.eval(env).asBool() ? body.eval(env) : elseBlock.eval(env);
    }

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
