/*
 * Decompiled with CFR 0.150.
 */
public class Not
implements Expression {
    private Expression exp;

    public Not(Expression expression) {
        this.exp = expression;
    }

    public Object eval(Environment environment) {
        Object object = this.exp.eval(environment);
        if (!(object instanceof Boolean)) {
            throw new JavaTownException("Cannot negate non-boolean value:  " + object);
        }
        if (((Boolean)object).booleanValue()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}

