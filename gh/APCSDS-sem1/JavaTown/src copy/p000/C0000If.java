package p000;

/* renamed from: If */
public class C0000If implements Statement {
    private Statement alt;
    private Statement cons;
    private Expression pred;

    public C0000If(Expression expression, Statement statement, Statement statement2) {
        this.pred = expression;
        this.cons = statement;
        this.alt = statement2;
    }

    public void exec(Environment environment) {
        if (((Boolean) this.pred.eval(environment)).booleanValue()) {
            this.cons.exec(environment);
        } else {
            this.alt.exec(environment);
        }
    }
}
