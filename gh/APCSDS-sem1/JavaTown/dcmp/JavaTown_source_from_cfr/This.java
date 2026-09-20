/*
 * Decompiled with CFR 0.150.
 */
public class This
implements Expression {
    public Object eval(Environment environment) {
        return environment.getOwner();
    }
}

