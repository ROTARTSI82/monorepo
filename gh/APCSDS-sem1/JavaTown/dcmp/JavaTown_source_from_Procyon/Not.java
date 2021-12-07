// 
// Decompiled by Procyon v0.5.36
// 

public class Not implements Expression
{
    private Expression exp;
    
    public Not(final Expression exp) {
        this.exp = exp;
    }
    
    public Object eval(final Environment environment) {
        final Object eval = this.exp.eval(environment);
        if (!(eval instanceof Boolean)) {
            throw new JavaTownException("Cannot negate non-boolean value:  " + eval);
        }
        if (eval) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
