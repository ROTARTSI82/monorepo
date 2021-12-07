/*
 * Decompiled with CFR 0.151.
 */
public class If
implements Statement {
    private Expression pred;
    private Statement cons;
    private Statement alt;

    public If(Expression expression, Statement statement, Statement statement2) {
        this.pred = expression;
        this.cons = statement;
        this.alt = statement2;
    }

    public void exec(Environment environment) {
        boolean bl = (Boolean)this.pred.eval(environment);
        if (bl) {
            this.cons.exec(environment);
        } else {
            this.alt.exec(environment);
        }
    }
}

