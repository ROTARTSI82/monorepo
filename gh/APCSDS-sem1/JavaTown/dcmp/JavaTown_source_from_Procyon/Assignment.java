// 
// Decompiled by Procyon v0.5.36
// 

public class Assignment implements Statement
{
    private String var;
    private Expression exp;
    
    public Assignment(final String var, final Expression exp) {
        this.var = var;
        this.exp = exp;
    }
    
    public void exec(final Environment environment) {
        environment.set(this.var, this.exp.eval(environment));
        World.redraw();
    }
}
