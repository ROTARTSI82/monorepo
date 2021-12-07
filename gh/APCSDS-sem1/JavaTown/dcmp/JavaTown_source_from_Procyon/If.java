// 
// Decompiled by Procyon v0.5.36
// 

public class If implements Statement
{
    private Expression pred;
    private Statement cons;
    private Statement alt;
    
    public If(final Expression pred, final Statement cons, final Statement alt) {
        this.pred = pred;
        this.cons = cons;
        this.alt = alt;
    }
    
    public void exec(final Environment environment) {
        if (this.pred.eval(environment)) {
            this.cons.exec(environment);
        }
        else {
            this.alt.exec(environment);
        }
    }
}
