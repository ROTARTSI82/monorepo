/*
 * Decompiled with CFR 0.151.
 */
public class Variable
implements Expression {
    private String name;

    public Variable(String string) {
        this.name = string;
    }

    public Object eval(Environment environment) {
        return environment.get(this.name);
    }
}

