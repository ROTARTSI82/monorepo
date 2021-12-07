package p000;

/* renamed from: Assignment */
public class Assignment implements Statement {
    private Expression exp;
    private String var;

    public Assignment(String str, Expression expression) {
        this.var = str;
        this.exp = expression;
    }

    public void exec(Environment environment) {
        environment.set(this.var, this.exp.eval(environment));
        World.redraw();
    }
}
