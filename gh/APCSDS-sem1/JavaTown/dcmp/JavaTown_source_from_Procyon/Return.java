// 
// Decompiled by Procyon v0.5.36
// 

public class Return implements Statement
{
    private Expression exp;
    
    public Return(final Expression exp) {
        this.exp = exp;
    }
    
    public void exec(final Environment environment) {
        throw new ReturnException(this.exp.eval(environment));
    }
}
