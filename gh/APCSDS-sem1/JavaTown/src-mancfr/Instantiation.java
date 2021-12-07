/*
 * Decompiled with CFR 0.151.
 */
import java.util.List;

public class Instantiation
implements Expression {
    private String name;
    private List args;

    public Instantiation(String string, List list) {
        this.name = string;
        this.args = list;
    }

    public Object eval(Environment environment) {
        return World.instantiate(this.name, this.args, environment);
    }
}

