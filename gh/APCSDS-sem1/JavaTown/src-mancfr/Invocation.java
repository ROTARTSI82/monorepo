/*
 * Decompiled with CFR 0.151.
 */
import java.util.List;

public class Invocation
implements Statement,
Expression {
    private Expression target;
    private String message;
    private List args;

    public Invocation(Expression expression, String string, List list) {
        this.target = expression;
        this.message = string;
        this.args = list;
    }

    public void exec(Environment environment) {
        this.eval(environment);
    }

    public Object eval(Environment environment) {
        Object object = this.target.eval(environment);
        if (object == null) {
            throw new JavaTownException("Null Pointer Exception:\nAttempt to pass message to null");
        }
        if (!(object instanceof Person)) {
            throw new JavaTownException("Attempt to pass message to primitive:  " + object);
        }
        return World.invoke((Person)object, this.message, this.args, environment);
    }
}

