package p000;

/* renamed from: Not */
public class Not implements Expression {
    private Expression exp;

    public Not(Expression expression) {
        this.exp = expression;
    }

    public Object eval(Environment environment) {
        Object eval = this.exp.eval(environment);
        if (!(eval instanceof Boolean)) {
            throw new JavaTownException("Cannot negate non-boolean value:  " + eval);
        } else if (((Boolean) eval).booleanValue()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
}
