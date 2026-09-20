package p000;

/* renamed from: Return */
public class Return implements Statement {
    private Expression exp;

    public Return(Expression expression) {
        this.exp = expression;
    }

    public void exec(Environment environment) {
        throw new ReturnException(this.exp.eval(environment));
    }
}
