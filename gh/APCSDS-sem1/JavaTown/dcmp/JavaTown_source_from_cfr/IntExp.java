/*
 * Decompiled with CFR 0.150.
 */
public class IntExp
implements Expression {
    private int value;

    public IntExp(int n) {
        this.value = n;
    }

    public Object eval(Environment environment) {
        return new Integer(this.value);
    }
}

