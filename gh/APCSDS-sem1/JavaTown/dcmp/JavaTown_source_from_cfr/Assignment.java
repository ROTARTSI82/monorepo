/*
 * Decompiled with CFR 0.150.
 */
public class Assignment
implements Statement {
    private String var;
    private Expression exp;

    public Assignment(String string, Expression expression) {
        this.var = string;
        this.exp = expression;
    }

    public void exec(Environment environment) {
        environment.set(this.var, this.exp.eval(environment));
        World.redraw();
    }
}

