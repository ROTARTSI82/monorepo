import java.util.List;

// 
// Decompiled by Procyon v0.5.36
// 

public class Invocation implements Statement, Expression
{
    private Expression target;
    private String message;
    private List args;
    
    public Invocation(final Expression target, final String message, final List args) {
        this.target = target;
        this.message = message;
        this.args = args;
    }
    
    public void exec(final Environment environment) {
        this.eval(environment);
    }
    
    public Object eval(final Environment environment) {
        final Object eval = this.target.eval(environment);
        if (eval == null) {
            throw new JavaTownException("Null Pointer Exception:\nAttempt to pass message to null");
        }
        if (!(eval instanceof Person)) {
            throw new JavaTownException("Attempt to pass message to primitive:  " + eval);
        }
        return World.invoke((Person)eval, this.message, this.args, environment);
    }
}
