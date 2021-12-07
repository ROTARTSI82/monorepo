/*
 * Decompiled with CFR 0.150.
 */
public class Return
implements Statement {
    private Expression exp;

    public Return(Expression expression) {
        this.exp = expression;
    }

    public void exec(Environment environment) {
        Object object = this.exp.eval(environment);
        throw new ReturnException(object);
    }
}

