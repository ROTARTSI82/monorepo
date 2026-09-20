package p000;

import java.util.List;

/* renamed from: Invocation */
public class Invocation implements Statement, Expression {
    private List args;
    private String message;
    private Expression target;

    public Invocation(Expression expression, String str, List list) {
        this.target = expression;
        this.message = str;
        this.args = list;
    }

    public void exec(Environment environment) {
        eval(environment);
    }

    public Object eval(Environment environment) {
        Object eval = this.target.eval(environment);
        if (eval == null) {
            throw new JavaTownException("Null Pointer Exception:\nAttempt to pass message to null");
        } else if (eval instanceof Person) {
            return World.invoke((Person) eval, this.message, this.args, environment);
        } else {
            throw new JavaTownException("Attempt to pass message to primitive:  " + eval);
        }
    }
}
