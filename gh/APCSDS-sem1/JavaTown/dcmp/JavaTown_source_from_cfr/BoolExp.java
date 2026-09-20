/*
 * Decompiled with CFR 0.150.
 */
public class BoolExp
implements Expression {
    private boolean value;

    public BoolExp(boolean bl) {
        this.value = bl;
    }

    public Object eval(Environment environment) {
        return this.value ? Boolean.TRUE : Boolean.FALSE;
    }
}

