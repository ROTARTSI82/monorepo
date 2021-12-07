/*
 * Decompiled with CFR 0.151.
 */
public class StringExp
implements Expression {
    private String value;

    public StringExp(String string) {
        this.value = string;
    }

    public Object eval(Environment environment) {
        return this.value;
    }
}

